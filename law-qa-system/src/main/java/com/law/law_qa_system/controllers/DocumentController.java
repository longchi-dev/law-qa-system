package com.law.law_qa_system.controllers;

import com.law.law_qa_system.api.ApiService;
import com.law.law_qa_system.models.*;
import com.law.law_qa_system.services.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/documents")
public class DocumentController {
    @Autowired
    private UserService userService;
    @Autowired
    private SavedDocumentService savedDocumentService;
    @Autowired
    private LegalDocumentService legalDocumentService;
    @Autowired
    private LegalDocumentDetailService legalDocumentDetailService;
    @Autowired
    private ElasticsearchService elasticsearchService;
    @Autowired
    private SearchHistoryService searchHistoryService;
    @Autowired
    private OpenAIService openAIService;

    @GetMapping
    public String getAllDocuments(Model model) {

        List<LegalDocument> documents = legalDocumentService.getAllLegalDocuments();
        if (documents.isEmpty()) {
            model.addAttribute("message", "No documents found.");
        }
        model.addAttribute("documents", documents);
        return "documents/list";
    }

    @GetMapping("/{id}")
    public String getDocumentById(@PathVariable Long id, Model model, Principal principal) {
        Optional<LegalDocument> legalDocumentOp = legalDocumentService.getLegalDocumentById(id);

        if (legalDocumentOp.isEmpty()) {
            model.addAttribute("error", "Document not found");
            return "error";
        }

        LegalDocument legalDocument = legalDocumentOp.get();
        List<LegalDocumentDetail> details = legalDocumentDetailService.getLegalDocumentDetailsByLegalDocumentId(legalDocument.getId());


        model.addAttribute("document", legalDocument);
        model.addAttribute("details", details);

        if (principal != null) {
            String email = principal.getName();
            User user = userService.getUserByEmail(email);
            model.addAttribute("user", user);
            boolean isSaved = savedDocumentService.isDocumentSaved(user.getId(), legalDocument.getId());
            model.addAttribute("isSaved", isSaved);
        } else {
            model.addAttribute("user", null);
            model.addAttribute("isSaved", false);
        }
        return "documents/detail";
    }

    // EDIT
//    @GetMapping("/edit/{id}")
//    public String updateDocumentForm(@PathVariable Long id, Model model) {
//        ResponseObject response = documentService.getDocumentById(id).getBody();
//        model.addAttribute("document", response.getData());
//        return "documents/edit";
//    }

    // DELETE
    @DeleteMapping("/{id}")
    public String deleteDocument(@PathVariable Long id, Model model) {
//        boolean isDeleted = documentService.deleteDocument(id);
        boolean isDeleted = legalDocumentService.deleteLegalDocument(id);
        if (!isDeleted) {
            model.addAttribute("error", "Document not found");
            return "error";
        }
        model.addAttribute("message", "Document deleted successfully");
        return "redirect:/documents/";
    }

    @GetMapping("/search")
    public String search(@RequestParam(required = false) String query, Model model, HttpSession session) {
        try {
            Account account = (Account) session.getAttribute("loggedInUser");
            if (account != null) {
                User user = userService.getUserByEmail(account.getEmail());
                searchHistoryService.saveSearchHistory(query, user.getId());
            }

            if (query == null || query.trim().isEmpty()) {
                List<LegalDocument> legalDocuments = legalDocumentService.getAllLegalDocuments();
                model.addAttribute("documents", legalDocuments);
            } else {
                // Tìm kiếm trong Elasticsearch
                List<Map<String, Object>> results = elasticsearchService.searchDocuments(query);
                model.addAttribute("documents", results);
            }
            model.addAttribute("query", query);
            return "TimKiem";
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tìm kiếm", e);
        }
    }



}
