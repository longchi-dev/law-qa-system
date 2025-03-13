package com.law.law_qa_system.repositories;

import com.law.law_qa_system.models.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
    List<SearchHistory> findByUserIdOrderBySearchTimeDesc(Long userId);
}
