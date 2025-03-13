package com.law.law_qa_system.services;

import com.law.law_qa_system.models.SearchHistory;
import com.law.law_qa_system.models.User;
import com.law.law_qa_system.repositories.SearchHistoryRepository;
import com.law.law_qa_system.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SearchHistoryService {
    @Autowired
    private SearchHistoryRepository searchHistoryRepository;
    @Autowired
    private UserRepository userRepository;

    public void saveSearchHistory(String query, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        SearchHistory searchHistory = SearchHistory.builder()
                .query(query)
                .searchTime(LocalDateTime.now())
                .user(user)
                .build();

        searchHistoryRepository.save(searchHistory);
    }

    public List<SearchHistory> getUserSearchHistory(Long userId) {
        return searchHistoryRepository.findByUserIdOrderBySearchTimeDesc(userId);
    }
}
