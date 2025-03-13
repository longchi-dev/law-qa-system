package com.law.law_qa_system.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.law.law_qa_system.enums.EnumTypes;
import com.law.law_qa_system.models.Document;
import com.law.law_qa_system.models.DocumentDetails;
import com.law.law_qa_system.models.dto.DocumentDTO;
import com.law.law_qa_system.services.DocumentDetailsService;
import com.law.law_qa_system.services.DocumentService;
import com.law.law_qa_system.services.PdfGenerationService;
import jakarta.annotation.PostConstruct;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ApiService {
    private final String BASE_URL = "https://vanbanphapluat.co/api";

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocumentDetailsService documentDetailsService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PdfGenerationService pdfGenerationService;

    public List<DocumentDTO> fetchDocumentFromApi() {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                    .path("/search")
                    .queryParam("r", 100)
                    .toUriString();

            JsonNode responseNode = restTemplate.getForObject(url, JsonNode.class);
            List<DocumentDTO> documentDTOs = new ArrayList<>();

            if (responseNode.has("Items")) {
                JsonNode itemsNode = responseNode.get("Items");
                System.out.println("Response: " + itemsNode);

                ObjectMapper objectMapper = new ObjectMapper();
                for (JsonNode item : itemsNode) {
                    DocumentDTO documentDTO = objectMapper.convertValue(item, DocumentDTO.class);
                    documentDTOs.add(documentDTO);
                }
            }

            return documentDTOs;
        } catch (Exception e) {
            System.err.println("Failed to fetch documents from API: " + e.getMessage());
            return Collections.emptyList();
        }
    }
    public Document convertDtoToDocument(DocumentDTO documentDTO) {
        String documentTypeTitle = documentDTO.getLoaiVanBan() != null ?
                removeHtmlTags(documentDTO.getLoaiVanBan().getTitle()) : "Luật";

        String signer = documentDTO.getNguoiKy() != null && !documentDTO.getNguoiKy().isEmpty() ?
                documentDTO.getNguoiKy().get(0).getTitle() : "";

        String keywords = String.join(", ", documentDTO.getLinhVuc().stream()
                .map(DocumentDTO.Field::getTitle)
                .toArray(String[]::new));

        String issuingAgencies = String.join(", ", documentDTO.getCoQuanBanHanh().stream()
                .map(DocumentDTO.Agency::getTitle)
                .toArray(String[]::new));

        String pdfContent = "";

        // FILE PDF
        String baseUrl = "https://vanbanphapluat.co";
        String sourceUrl = baseUrl + documentDTO.getUid();
        String soHieu = documentDTO.getSoHieu();
        String fileName = sanitizeFileName(soHieu) + ".pdf";
        String filePath = "/PDF/" + fileName;

        File pdfFile = new File(System.getProperty("user.dir") + filePath);
        if (!pdfFile.exists()) {
            try {
                pdfGenerationService.generatePdfFromHtml(sourceUrl, fileName);
                System.out.println("PDF created successfully: " + fileName);
            } catch (Exception e) {
                System.err.println("Failed to generate PDF for source URL: " + sourceUrl + ". Error: " + e.getMessage());
            }
        } else {
            System.out.println("PDF already exists: " + fileName);
        }

        if (pdfFile.exists()) {
            try (PDDocument pdfDocument = PDDocument.load(pdfFile)) {
                PDFTextStripper stripper = new PDFTextStripper();
                pdfContent = stripper.getText(pdfDocument);
            } catch (Exception e) {
                System.err.println("Failed to read PDF content: " + e.getMessage());
            }
        }

        // Document
        Document document = Document.builder()
                .title(documentDTO.getTitle())
                .content(documentDTO.getTrichYeu())
                .pdfContent(pdfContent)
                .keywords(keywords)
                .sourceUrl(filePath)
                .author(issuingAgencies)
                .number(documentDTO.getSoHieu())
                .signer(signer)
                .issueDate(documentDTO.getNgayBanHanh())
                .effectiveDate(documentDTO.getNgayHieuLuc())
                .updateDate(documentDTO.getUpdated())
                .type(documentTypeTitle)
                .build();

        // DocumentDetails
        if (documentDTO.getCoQuanBanHanh() != null && !documentDTO.getCoQuanBanHanh().isEmpty()) {
            if (document.getDocumentDetails() == null) {
                document.setDocumentDetails(new ArrayList<>());
            }

            for (DocumentDTO.Agency agency : documentDTO.getCoQuanBanHanh()) {
                String applyStatus = documentDTO.getTrinhTrangHieuLuc() != null ?
                        documentDTO.getTrinhTrangHieuLuc().getTitle() : "Không xác định";
                DocumentDetails documentDetails = DocumentDetails.builder()
                        .document(document)
                        .agency(agency.getTitle())
                        .applyStatus(applyStatus)
                        .validityStatus(applyStatus)
                        .build();

                document.getDocumentDetails().add(documentDetails);
            }
        }

        document = documentService.createDocument(document);

        if (document.getDocumentDetails() != null) {
            for (DocumentDetails details : document.getDocumentDetails()) {
                documentDetailsService.createDocumentDetails(details);
            }
        }

        return document;
    }
    public void saveDocumentsToDatabase() {
        List<DocumentDTO> documentDTOs = fetchDocumentFromApi();
        if (documentDTOs != null && !documentDTOs.isEmpty()) {
            for (DocumentDTO documentDTO : documentDTOs) {
                try {
                    if (!documentService.existsByTitle(documentDTO.getTitle())) {
                        Document document = convertDtoToDocument(documentDTO);
                        documentService.createDocument(document);
                    } else {
                        System.out.println("Document already exists: " + documentDTO.getTitle());
                    }
                } catch (Exception e) {
                    System.err.println("Error saving document: " + documentDTO.getTitle() + " - " + e.getMessage());
                }
            }
        } else {
            System.out.println("No legal documents available to save.");
        }
    }
    private String removeHtmlTags(String input) {
        if (input != null) {
            return input.replaceAll("<[^>]*>", "");
        }
        return "";
    }
    private String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[\\\\/:*?\"<>|]", "_");
    }
//    @PostConstruct
//    public void initialize() {
//        try {
//            saveDocumentsToDatabase();
//            System.out.println("Fetch and save documents");
//
//        } catch (Exception e) {
//            System.err.println("Failed to fetch and save documents: " + e.getMessage());
//        }
//    }
}
