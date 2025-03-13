package com.law.law_qa_system.services;

import com.itextpdf.html2pdf.HtmlConverter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class PdfGenerationService {
    @Autowired
    private RestTemplate restTemplate;
    private final String BASE_DIRECTORY = "src/main/resources/static/PDF";
    public void generatePdfFromHtml(String sourceUrl, String fileName) {
        try {
            if (!fileName.endsWith(".pdf")) {
                fileName += ".pdf";
            }
            String htmlContent = restTemplate.getForObject(sourceUrl, String.class);

            Document doc = Jsoup.parse(htmlContent);

            Element content = doc.select("div[itemprop=articleBody]").first();

            if (content != null) {
                String filteredHtml = content.html();

                String fontStyle = "<style>body { font-family: 'Times New Roman', Times, serif; }</style>";
                filteredHtml = fontStyle + filteredHtml;

                Path directoryPath = Paths.get(BASE_DIRECTORY);
                Path filePath = directoryPath.resolve(fileName);

                if (!Files.exists(directoryPath)) {
                    Files.createDirectories(directoryPath);
                }

                try (FileOutputStream os = new FileOutputStream(filePath.toFile())) {
                    HtmlConverter.convertToPdf(filteredHtml, os);
                }

                System.out.println("PDF created successfully at: " + filePath.toAbsolutePath());
            } else {
                System.out.println("Content not found in HTML.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
