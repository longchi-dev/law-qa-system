package com.law.law_qa_system.repositories;

import com.law.law_qa_system.models.LegalDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LegalDocumentRepository extends JpaRepository<LegalDocument, Long> {
    Optional<LegalDocument> findByTitle(String title);


    @Query("SELECT ld FROM LegalDocument ld LEFT JOIN FETCH ld.legalDocumentDetail ldd")
    List<LegalDocument> findAllWithDetails();

    @Query("SELECT ld FROM LegalDocument ld LEFT JOIN FETCH ld.legalDocumentDetail ldd ORDER BY ldd.issuedDate DESC")
    List<LegalDocument> findTop10ByOrderByIssuedDateDesc();

    @Query("SELECT ld FROM LegalDocument ld LEFT JOIN FETCH ld.legalDocumentDetail ldd WHERE ldd.documentType = 'Công văn' ORDER BY ldd.issuedDate DESC")
    List<LegalDocument> findTop10OfficialDocuments();

    @Query("SELECT ld FROM LegalDocument ld LEFT JOIN FETCH ld.legalDocumentDetail ldd WHERE ldd.documentType = 'Thông tư' ORDER BY ldd.issuedDate DESC")
    List<LegalDocument> findTop10CircularDocuments();

    @Query("SELECT ld FROM LegalDocument ld LEFT JOIN FETCH ld.legalDocumentDetail ldd WHERE ldd.documentType = 'Quyết định' ORDER BY ldd.issuedDate DESC")
    List<LegalDocument> findTop10DecisionDocuments();

}
