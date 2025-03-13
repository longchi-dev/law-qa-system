package com.law.law_qa_system.repositories;

import com.law.law_qa_system.models.DocumentDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentDetailsRepository extends JpaRepository<DocumentDetails, Long> {
    List<DocumentDetails> findByDocumentId(Long documentId);
    List<DocumentDetails> findByGazetteNumber(String gazetteNumber);
    List<DocumentDetails> findByApplyStatus(String applyStatus);
    List<DocumentDetails> findByField(String field);
    List<DocumentDetails> findByValidityStatus(String validityStatus);

    List<DocumentDetails> findByAgency(String agency);
}
