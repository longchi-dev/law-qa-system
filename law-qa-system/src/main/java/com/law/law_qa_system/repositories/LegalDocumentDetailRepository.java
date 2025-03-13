package com.law.law_qa_system.repositories;

import com.law.law_qa_system.models.LegalDocumentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LegalDocumentDetailRepository extends JpaRepository<LegalDocumentDetail, Long> {
    boolean existsByDetailUrl(String detailUrl);

    List<LegalDocumentDetail> findByLegalDocumentId(Long documentId);

}
