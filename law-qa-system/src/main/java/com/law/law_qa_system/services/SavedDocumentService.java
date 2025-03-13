package com.law.law_qa_system.services;

import com.law.law_qa_system.models.Document;
import com.law.law_qa_system.models.LegalDocument;
import com.law.law_qa_system.models.SavedDocument;
import com.law.law_qa_system.models.User;
import com.law.law_qa_system.repositories.DocumentRepository;
import com.law.law_qa_system.repositories.LegalDocumentRepository;
import com.law.law_qa_system.repositories.SavedDocumentRepository;
import com.law.law_qa_system.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SavedDocumentService {
    @Autowired
    private SavedDocumentRepository savedDocumentRepository;
    @Autowired
    private LegalDocumentRepository legalDocumentRepository;
    @Autowired
    private UserRepository userRepository;

    public void saveDocument(Long userId, Long documentId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        LegalDocument document = legalDocumentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        if (savedDocumentRepository.findByUserAndLegalDocument(user, document).isPresent()) {
            return;
        }

        SavedDocument savedDocument = SavedDocument.builder()
                .user(user)
                .legalDocument(document)
                .savedAt(LocalDateTime.now())
                .build();

        savedDocumentRepository.save(savedDocument);
    }

    public void removeSavedDocument(Long userId, Long documentId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        LegalDocument document = legalDocumentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        Optional<SavedDocument> savedDocument = savedDocumentRepository.findByUserAndLegalDocument(user, document);
        if (savedDocument.isPresent()) {
            savedDocumentRepository.delete(savedDocument.get());
        }
    }

    public List<LegalDocument> getSavedDocuments(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return savedDocumentRepository.findByUser(user).stream()
                .map(SavedDocument::getLegalDocument)
                .collect(Collectors.toList());
    }

    public boolean isDocumentSaved(Long userId, Long documentId) {
        return savedDocumentRepository.existsByUserIdAndLegalDocumentId(userId, documentId);
    }

    @Transactional
    public void removeAllSavedDocuments(Long userId) {
        savedDocumentRepository.deleteByUserId(userId);
    }
}
