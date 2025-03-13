package com.law.law_qa_system.services;

import com.law.law_qa_system.models.LegalDocument;
import com.law.law_qa_system.repositories.LegalDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LegalDocumentService {
    @Autowired
    private LegalDocumentRepository legalDocumentRepository;
    @Autowired
    private LegalDocumentDetailService legalDocumentDetailService;

    public List<LegalDocument> getAllLegalDocuments() {
        return legalDocumentRepository.findAllWithDetails();
    }

    public Optional<LegalDocument> getLegalDocumentById(Long id) {
        return legalDocumentRepository.findById(id);
    }

    public Optional<LegalDocument> getLegalDocumentByTitle(String title) {
        return legalDocumentRepository.findByTitle(title);
    }


    public List<LegalDocument> getLatestDocuments() {
        List<LegalDocument> top10LegalDocuments = legalDocumentRepository.findTop10ByOrderByIssuedDateDesc();
        top10LegalDocuments = top10LegalDocuments.stream().limit(10).collect(Collectors.toList());
        return top10LegalDocuments;
    }

    public List<LegalDocument> getTop10OfficialDocuments() {
        List<LegalDocument> top10LegalDocuments = legalDocumentRepository.findTop10OfficialDocuments();
        top10LegalDocuments = top10LegalDocuments.stream().limit(10).collect(Collectors.toList());
        return top10LegalDocuments;
    }

    public List<LegalDocument> getTop10CircularDocuments() {
        List<LegalDocument> top10LegalDocuments = legalDocumentRepository.findTop10CircularDocuments();
        top10LegalDocuments = top10LegalDocuments.stream().limit(10).collect(Collectors.toList());
        return top10LegalDocuments;
    }

    public List<LegalDocument> getTop10DecisionDocuments() {
        List<LegalDocument> top10LegalDocuments = legalDocumentRepository.findTop10DecisionDocuments();
        top10LegalDocuments = top10LegalDocuments.stream().limit(10).collect(Collectors.toList());
        return top10LegalDocuments;
    }

    public void saveAll(List<LegalDocument> documents) {
        legalDocumentRepository.saveAll(documents);
    }

    public boolean deleteLegalDocument(Long id) {
        if (legalDocumentRepository.existsById(id)) {
            legalDocumentDetailService.deleteLegalDocumentDetails(id);
            legalDocumentRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
