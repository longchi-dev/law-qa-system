package com.law.law_qa_system.services;

import com.law.law_qa_system.enums.EnumTypes;
import com.law.law_qa_system.models.Document;
import com.law.law_qa_system.models.ResponseObject;
import com.law.law_qa_system.repositories.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DocumentService {
    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DocumentDetailsService documentDetailsService;

    @Autowired
    private OllamaEmbeddingService ollamaEmbeddingService;

    @Autowired
    private DocumentEmbeddingService documentEmbeddingService;

    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    public Optional<Document> getDocumentById(Long id) {
        return documentRepository.findById(id);
    }

    public Document createDocument(Document document) {
        document = documentRepository.save(document);

        List<Double> embedding = ollamaEmbeddingService.getEmbedding(document.getContent());
    //    documentEmbeddingService.saveEmbedding(document, embedding);

        return document;
    }

    public Document updateDocument(Long id, Document document) {
        if (documentRepository.existsById(id)) {
            document.setId(id);
            return documentRepository.save(document);
        }
        return null;
    }

    public boolean deleteDocument(Long id) {
        if (documentRepository.existsById(id)) {
            documentDetailsService.deleteDocumentDetails(id);
            documentRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<Document> getDocumentByTitle(String title) {
        return documentRepository.findByTitle(title);
    }

    public List<Document> getDocumentsByKeyword(String keyword) {
        return documentRepository.findByKeywords(keyword);
    }

    public List<Document> getLatestDocuments() {
        return documentRepository.findTop10ByOrderByIssueDateDesc();
    }

    public Optional<Document> getDocumentByTitleIgnoreCase(String title) {
        return documentRepository.findByTitleIgnoreCase(title);
    }

    public List<Document> getDocumentsByKeywordContainingIgnoreCase(String keyword) {
        return documentRepository.findByKeywordsContainingIgnoreCase(keyword);
    }

    public List<Document> getDocumentsByType(String type) {
        return documentRepository.findByType(type);
    }

    public List<Document> getDocumentsByTypeSortedByIssueDateDesc(String type) {
        return documentRepository.findByTypeOrderByIssueDateDesc(type);
    }

    public Page<Document> getDocumentsByTypeWithPagination(String type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return documentRepository.findByType(type, pageable);
    }

    public List<Document> getFeaturedDocuments() {
        return documentRepository.findByIsFeaturedTrue();
    }

    public Page<Document> getFeaturedDocumentsWithPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return documentRepository.findByIsFeaturedTrue(pageable);
    }

    public List<Document> findByIssueDate(String issueDate) {
        LocalDateTime parsedDate = LocalDateTime.parse(issueDate);
        return documentRepository.findByIssueDate(parsedDate);
    }

    public List<Document> findByIssueDateBetween(String startDate, String endDate) {
        LocalDateTime parsedStartDate = LocalDateTime.parse(startDate);
        LocalDateTime parsedEndDate = LocalDateTime.parse(endDate);
        return documentRepository.findByIssueDateBetween(parsedStartDate, parsedEndDate);
    }

    public Optional<Document> findByNumber(String number) {
        return documentRepository.findByNumber(number);
    }

    public boolean existsByNumber(String number) {
        return documentRepository.existsByNumber(number);
    }

    public List<Document> getDocumentsByTypeAndFeatured(String type) {
        return documentRepository.findByTypeAndIsFeaturedTrue(type);
    }

    public boolean existsByTitle(String title) {
        return documentRepository.existsByTitle(title);
    }

    public List<Document> searchDocumentsByGeneralCriteria(String query) {
        return documentRepository.findByTitleContainingIgnoreCaseOrNumberContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrSignerContainingIgnoreCase(
                query, query, query, query
        );
    }

}
