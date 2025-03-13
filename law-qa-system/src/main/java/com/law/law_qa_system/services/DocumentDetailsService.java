package com.law.law_qa_system.services;

import com.law.law_qa_system.models.DocumentDetails;
import com.law.law_qa_system.models.ResponseObject;
import com.law.law_qa_system.repositories.DocumentDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DocumentDetailsService {

    @Autowired
    private DocumentDetailsRepository documentDetailsRepository;

    public List<DocumentDetails> getAllDocumentDetails() {
        return documentDetailsRepository.findAll();
    }

    public Optional<DocumentDetails> getDocumentDetailsById(Long id) {
        return documentDetailsRepository.findById(id);
    }

    public List<DocumentDetails> getDocumentDetailsByDocumentId(Long documentId) {
        return documentDetailsRepository.findByDocumentId(documentId);
    }

    public List<DocumentDetails> getDocumentDetailsByGazetteNumber(String gazetteNumber) {
        return documentDetailsRepository.findByGazetteNumber(gazetteNumber);
    }

    public List<DocumentDetails> getDocumentDetailsByApplyStatus(String applyStatus) {
        return documentDetailsRepository.findByApplyStatus(applyStatus);
    }

    public List<DocumentDetails> getDocumentDetailsByField(String field) {
        return documentDetailsRepository.findByField(field);
    }

    public List<DocumentDetails> getDocumentDetailsByValidityStatus(String validityStatus) {
        return documentDetailsRepository.findByValidityStatus(validityStatus);
    }

    public DocumentDetails createDocumentDetails(DocumentDetails documentDetails) {
        return documentDetailsRepository.save(documentDetails);
    }

    public DocumentDetails updateDocumentDetails(Long id, DocumentDetails documentDetails) {
        if (documentDetailsRepository.existsById(id)) {
            documentDetails.setId(id);
            return documentDetailsRepository.save(documentDetails);
        }
        return null;
    }

    public boolean deleteDocumentDetails(Long id) {
        if (documentDetailsRepository.existsById(id)) {
            documentDetailsRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<DocumentDetails> getDocumentDetailsByAgency(String agency) {
        return documentDetailsRepository.findByAgency(agency); // Trả về danh sách thay vì Optional
    }
}
