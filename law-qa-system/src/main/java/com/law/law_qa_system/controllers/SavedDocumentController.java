package com.law.law_qa_system.controllers;

import com.law.law_qa_system.models.Document;
import com.law.law_qa_system.models.LegalDocument;
import com.law.law_qa_system.models.User;
import com.law.law_qa_system.services.SavedDocumentService;
import com.law.law_qa_system.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/saved-documents")
public class SavedDocumentController {
    @Autowired
    private SavedDocumentService savedDocumentService;
    @Autowired
    private UserService userService;

    @PostMapping("/save")
    public String saveDocument(@RequestParam Long userId, @RequestParam Long documentId) {
        savedDocumentService.saveDocument(userId, documentId);
        return "redirect:/user/thong-tin-tai-khoan";
    }

    @PostMapping("/remove")
    public String removeDocument(@RequestParam Long userId, @RequestParam Long documentId) {
        savedDocumentService.removeSavedDocument(userId, documentId);
        return "redirect:/user/thong-tin-tai-khoan";
    }

    @GetMapping("/list")
    public String listSavedDocuments(@RequestParam Long userId, Model model) {
        List<LegalDocument> documents = savedDocumentService.getSavedDocuments(userId);
        model.addAttribute("documents", documents);
        return "redirect:/user/thong-tin-tai-khoan";
    }

    @PostMapping("/remove-all")
    public String removeAllSavedDocuments(Principal principal) {
        String email = principal.getName();
        User user = userService.getUserByEmail(email);

        if (user != null) {
            savedDocumentService.removeAllSavedDocuments(user.getId());
        }

        return "redirect:/user/thong-tin-tai-khoan";
    }

}
