package com.law.law_qa_system.repositories;

import com.law.law_qa_system.models.ChatDetailHistory;
import com.law.law_qa_system.models.ChatHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatDetailRepository extends JpaRepository<ChatDetailHistory, Long> {
    List<ChatDetailHistory> findByChatHistory(ChatHistory chatHistory);
    Optional<ChatDetailHistory> findById(Long id);
    void deleteByChatHistoryId(Long chatId);
}
