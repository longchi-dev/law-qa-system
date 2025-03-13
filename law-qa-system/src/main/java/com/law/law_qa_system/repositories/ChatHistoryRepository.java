package com.law.law_qa_system.repositories;

import com.law.law_qa_system.models.ChatHistory;
import com.law.law_qa_system.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long> {
    List<ChatHistory> findByUserIdOrderByStartTimeDesc(Long userId);
}

