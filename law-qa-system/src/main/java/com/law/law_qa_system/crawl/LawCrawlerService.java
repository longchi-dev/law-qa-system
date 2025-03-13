package com.law.law_qa_system.crawl;

import com.law.law_qa_system.models.LegalDocument;
import com.law.law_qa_system.models.LegalDocumentDetail;
import com.law.law_qa_system.services.DocumentEmbeddingService;
import com.law.law_qa_system.services.LegalDocumentDetailService;
import com.law.law_qa_system.services.LegalDocumentService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class LawCrawlerService {
    private static final int THREAD_POOL_SIZE = 10;
    private static final int TIMEOUT = 15000;
    private static final String BASE_URL = "https://luatvietnam.vn/tim-van-ban.html?Keywords=&SearchOptions=1&SearchByDate=issueDate&DateFrom=&DateTo=";

    @Autowired
    private LegalDocumentService legalDocumentService;
    @Autowired
    private DocumentEmbeddingService documentEmbeddingService;

    @Autowired
    private LegalDocumentDetailService legalDocumentDetailService;

    public void crawlLawList() {
        try {
            Set<String> existUrls = new HashSet<>();

            List<LegalDocument> newDocuments = new ArrayList<>();

            int page = 1;
            int maxPages = 10;

            while(page < maxPages) {
                String url = BASE_URL + "&page=" + page;
                Document doc = Jsoup.connect(url).get();
                Elements articles = doc.select(".doc-article");

                for (Element article : articles) {
                    String title = article.select(".doc-title a").text();
                    String detailUrl = "https://luatvietnam.vn" + article.select(".doc-title a").attr("href");
                    Element dateElement = article.select(".w-doc-dmy2").first();
                    String issueDate = (dateElement != null) ? dateElement.text() : "N/A";

                    if (!existUrls.contains(detailUrl)) {
                        LegalDocument legalDocument = LegalDocument.builder()
                                .title(title)
                                .detailUrl(detailUrl)
                                .issueDate(issueDate)
                                .build();

                        newDocuments.add(legalDocument);
                        existUrls.add(detailUrl);
                    }
                }
                page++;
            }

            if (!newDocuments.isEmpty()) {
                legalDocumentService.saveAll(newDocuments);

                System.out.println("Đã lưu " + newDocuments.size() + " văn bản mới.");
                crawlLawDetails();
            } else {
                System.out.println("Không có văn bản mới.");
            }
        } catch (IOException e) {
            System.err.println("Lỗi khi crawl dữ liệu: " + e.getMessage());
        }
    }

    private void crawlLawDetails() {
        List<LegalDocument> documents = legalDocumentService.getAllLegalDocuments()
                .stream()
                .filter(doc -> !legalDocumentDetailService.existsByDetailUrl(doc.getDetailUrl()))
                .collect(Collectors.toList());

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        for (LegalDocument doc : documents) {
            executor.execute(() -> processDocumentDetail(doc));
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.MINUTES)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        System.out.println("Completed document detail crawling.");
    }

    private void processDocumentDetail(LegalDocument doc) {
        if (legalDocumentDetailService.existsByDetailUrl(doc.getDetailUrl())) return;

        try {
            Document detailPage = Jsoup.connect(doc.getDetailUrl()).timeout(TIMEOUT).get();
            Element contentElement = detailPage.select("div.the-document-body.noidungtracuu").first();
            Element titleElement = detailPage.selectFirst("h1.the-document-title");

            // DETAIL
            String title = titleElement != null ? titleElement.text() : "Không tìm thấy tiêu đề";
            String content = (contentElement != null) ? contentElement.text() : "";
            String issuingAgency = extractTableText(detailPage, "Cơ quan ban hành:");
            String officialGazetteNumber = extractTableText(detailPage, "Số công báo:");
            String documentNumber = extractTableText(detailPage, "Số hiệu:");
            String publicationDate = extractTableText(detailPage, "Ngày đăng công báo:");
            String documentType = extractTableText(detailPage, "Loại văn bản:");
            Element signerElement = detailPage.select("td:contains(Người ký:)").next().first();
            String signer = signerElement != null ? signerElement.text() : "Không có thông tin";
            String issuedDate = extractTableText(detailPage, "Ngày ban hành:");
            String effectiveDate = extractTableText(detailPage, "Ngày hết hiệu lực:");
            String fields = extractFields(detailPage);
            String pdfUrl = extractPdfUrl(detailPage);

            if (content.isEmpty()) {
                System.out.println("Không có nội dung hợp lệ, lưu 'Không có nội dung' cho: " + doc.getDetailUrl());
            }

//            LegalDocumentDetail detail = new LegalDocumentDetail(doc.getDetailUrl(),content, issuingAgency, officialGazetteNumber, publicationDate, documentType, signer, title, issuedDate, documentNumber, pdfUrl, fields);

            LegalDocumentDetail legalDocumentDetail = LegalDocumentDetail.builder()
                    .detailUrl(doc.getDetailUrl())
                    .content(content.isEmpty() ? "Không có nội dung" : content)
                    .issuingAgency(issuingAgency)
                    .officialGazetteNumber(officialGazetteNumber)
                    .documentNumber(documentNumber)
                    .publicationDate(publicationDate)
                    .documentType(documentType)
                    .signer(signer)
                    .title(title)
                    .issuedDate(issuedDate.isEmpty() || issuedDate.equals("Đang cập nhật") ? null : issuedDate)
                    .effectiveDate(effectiveDate.isEmpty() || effectiveDate.equals("Đang cập nhật") ? null : effectiveDate)
                    .fields(fields)
                    .pdfUrl(pdfUrl)
                    .legalDocument(doc)
                    .build();

            legalDocumentDetailService.save(legalDocumentDetail);
            documentEmbeddingService.processAndSaveEmbeddings(legalDocumentDetail.getId());
            System.out.println("Saved details for: " + doc.getDetailUrl());
        } catch (IOException e) {
            System.err.println("Lỗi khi lấy nội dung: " + doc.getDetailUrl() + " - " + e.getMessage());
        }
    }

    private String extractTableText(Document doc, String label) {
        Elements rows = doc.select("table.table-bordered tr");
        for (Element row : rows) {
            Elements cells = row.select("td");
            if (cells.size() >= 2 && cells.get(0).text().contains(label)) {
                return cells.get(1).text().trim();
            }
        }
        return "Đang cập nhật";
    }

    private String extractFields(Document doc) {
        Elements fieldElements = doc.select("td:has(a.tag-link) a.tag-link");
        List<String> fieldList = new ArrayList<>();
        for (Element field : fieldElements) {
            fieldList.add(field.text().trim());
        }
        return String.join(", ", fieldList);
    }

    private String extractPdfUrl(Document doc) {
        Element embedElement = doc.select("div.embedContent").first();
        if (embedElement != null) {
            return embedElement.attr("data-url");
        }
        return "Không có file PDF";
    }
}
