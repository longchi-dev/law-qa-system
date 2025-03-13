package com.law.law_qa_system.services;

import com.law.law_qa_system.models.Document;
import com.law.law_qa_system.models.DocumentEmbedding;
import com.law.law_qa_system.repositories.DocumentEmbeddingRepository;
import com.law.law_qa_system.util.VectorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {
    @Autowired
    private DocumentEmbeddingRepository documentEmbeddingRepository;

    @Autowired
    private DocumentEmbeddingService documentEmbeddingService;

    @Autowired
    private OllamaEmbeddingService ollamaEmbeddingService;

  /**  public List<Document> searchDocuments(String query, boolean advancedSearch, String searchOption, String documentType, String author, String signer, double threshold) {
        List<Double> queryEmbedding = ollamaEmbeddingService.getEmbedding(query);
        if (queryEmbedding.isEmpty()) {
            System.out.println("Query embedding retrieval failed.");
            return List.of();
        }

        List<DocumentEmbedding> allEmbeddings = documentEmbeddingRepository.findAll();

        return allEmbeddings.stream()
                .map(docEmbed -> {
                    List<Double> documentEmbedding = documentEmbeddingService.getEmbeddingByDocumentId(docEmbed.getDocument().getId());
                    if (documentEmbedding.isEmpty()) {
                        return null;
                    }
                    double similarity = VectorUtil.cosineSimilarity(queryEmbedding, documentEmbedding);
                    return new MatchedDocument(docEmbed.getDocument(), similarity);
                })
                .filter(match -> match != null && match.getSimilarity() >= threshold)
                .filter(match -> !advancedSearch || filterAdvancedSearch(match.getDocument(), query, searchOption, documentType, author, signer))
                .sorted((a, b) -> Double.compare(b.getSimilarity(), a.getSimilarity()))
                .map(MatchedDocument::getDocument)
                .collect(Collectors.toList());
    }
**/
    private boolean filterAdvancedSearch(Document doc, String query, String searchOption, String documentType, String author, String signer) {
        boolean matchesOption = searchOption == null || searchOption.equals("Tất cả") ||
                (searchOption.equals("Tiêu đề") && doc.getTitle() != null && doc.getTitle().toLowerCase().contains(query.toLowerCase())) ||
                (searchOption.equals("Số hiệu văn bản") && doc.getNumber() != null && doc.getNumber().toLowerCase().contains(query.toLowerCase()));

        boolean matchesType = documentType == null || documentType.isEmpty() || documentType.equals(doc.getType());
        boolean matchesAuthor = author == null || author.isEmpty() || author.equals(doc.getAuthor());
        boolean matchesSigner = signer == null || signer.isEmpty() || signer.equals(doc.getSigner());

        return matchesOption && matchesType && matchesAuthor && matchesSigner;
    }

    private static class MatchedDocument {
        private final Document document;
        private final double similarity;

        public MatchedDocument(Document document, double similarity) {
            this.document = document;
            this.similarity = similarity;
        }

        public Document getDocument() {
            return document;
        }

        public double getSimilarity() {
            return similarity;
        }
    }
}
