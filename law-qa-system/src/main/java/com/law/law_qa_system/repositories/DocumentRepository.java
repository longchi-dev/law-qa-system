package com.law.law_qa_system.repositories;

import com.law.law_qa_system.models.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    // Tìm kiếm theo tiêu đề
    Optional<Document> findByTitle(String title);
    Optional<Document> findByTitleIgnoreCase(String title);

    // Tìm kiếm theo từ khóa (hỗ trợ chứa từ khóa, không phân biệt chữ hoa/thường)
    List<Document> findByKeywords(String keyword);
    List<Document> findByKeywordsContainingIgnoreCase(String keyword);

    // Tìm kiếm theo loại tài liệu
    List<Document> findByType(String type);
    List<Document> findByTypeOrderByIssueDateDesc(String type);
    Page<Document> findByType(String type, Pageable pageable);

    // Tìm kiếm theo ngày ban hành
    List<Document> findTop10ByOrderByIssueDateDesc();
    List<Document> findByIssueDate(LocalDateTime issueDate);
    List<Document> findByIssueDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    Optional<Document> findByNumber(String number);
    boolean existsByNumber(String number);

    List<Document> findByAuthorAndIssueDateBetween(String author, LocalDateTime startDate, LocalDateTime endDate);

    List<Document> findByIsFeaturedTrue();
    Page<Document> findByIsFeaturedTrue(Pageable pageable);

    List<Document> findByTypeAndIsFeaturedTrue(String type);

    boolean existsByTitle(String title);

    List<Document> findByTitleContainingIgnoreCaseOrNumberContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrSignerContainingIgnoreCase(String query, String query1, String query2, String query3);
}
