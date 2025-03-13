package com.law.law_qa_system.repositories;


import com.law.law_qa_system.models.DocumentDetailEmbeddingES;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentDetailEmbeddingESRepository extends ElasticsearchRepository<DocumentDetailEmbeddingES, String> {
    List<DocumentDetailEmbeddingES> findByDocumentId(Long documentId);
}
