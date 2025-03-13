package com.law.law_qa_system.repositories;

import com.law.law_qa_system.models.Document;
import com.law.law_qa_system.models.LegalDocument;
import com.law.law_qa_system.models.SavedDocument;
import com.law.law_qa_system.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SavedDocumentRepository extends JpaRepository<SavedDocument, Long> {
    List<SavedDocument> findByUser(User user);
    Optional<SavedDocument> findByUserAndLegalDocument(User user, LegalDocument document);
    boolean existsByUserIdAndLegalDocumentId(Long userId, Long legalDocumentId);
    void deleteByUserId(Long userId);
}
