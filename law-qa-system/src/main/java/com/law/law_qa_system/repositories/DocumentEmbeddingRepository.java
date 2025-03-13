package com.law.law_qa_system.repositories;

import com.law.law_qa_system.models.DocumentEmbedding;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DocumentEmbeddingRepository extends JpaRepository<DocumentEmbedding, Long> {
    Optional<DocumentEmbedding> findByDocumentId(Long documentId);
}
