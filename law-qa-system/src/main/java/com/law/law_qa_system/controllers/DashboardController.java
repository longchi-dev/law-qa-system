package com.law.law_qa_system.controllers;

import com.law.law_qa_system.models.Document;
import com.law.law_qa_system.models.DocumentDetails;
import com.law.law_qa_system.models.LegalDocument;
import com.law.law_qa_system.models.User;
import com.law.law_qa_system.services.DocumentDetailsService;
import com.law.law_qa_system.services.DocumentService;
import com.law.law_qa_system.services.LegalDocumentService;
import com.law.law_qa_system.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class DashboardController {
    @Autowired
    private UserService userService;

    @Autowired
    private LegalDocumentService legalDocumentService;

    @GetMapping({"/", "/trang-chu"})
    public String showDashboard(Model model, Principal principal, HttpServletRequest request) {
        if (principal != null) {
            String email = principal.getName();
            User user = userService.getUserByEmail(email); // Lấy thông tin User từ DB
            model.addAttribute("user", user); // Truyền User vào Model
        } else {
            model.addAttribute("user", null); // Nếu không có Principal, gán null
        }

//        List<Document> documents = documentService.getAllDocuments();
//        documents.sort(Comparator.comparing(Document::getIssueDate).reversed());
//
//        List<Document> newDocuments = documentService.getLatestDocuments();

        // Nhóm tài liệu theo từng loại
//        Map<String, List<Document>> documentsByType = documents.stream()
//                .collect(Collectors.groupingBy(Document::getType));

        // Công văn, UBND, Dự thảo
//        List<Document> officialDocuments = documentsByType.getOrDefault("Công văn", Collections.emptyList());
//        List<Document> circularDocuments = documentsByType.getOrDefault("Thông tư", Collections.emptyList());
//        List<Document> decisionDocuments = documentsByType.getOrDefault("Quyết định", Collections.emptyList());

//        for (Document document : documents) {
//            List<DocumentDetails> details = documentDetailsService.getDocumentDetailsByDocumentId(document.getId());
//            document.setDocumentDetails(details);
//
//            List<String> keywordsList = Arrays.asList(document.getKeywords().split(","));
//            model.addAttribute("keywordsList", keywordsList);
//        }

//        model.addAttribute("documents", documents);
//        model.addAttribute("newDocuments", newDocuments);
//        model.addAttribute("officialDocuments", officialDocuments);
//        model.addAttribute("circularDocuments", circularDocuments);
//        model.addAttribute("decisionDocuments", decisionDocuments);

//        HttpSession session = request.getSession();
//        Boolean paymentSuccess = (Boolean) session.getAttribute("paymentSuccess");
//        System.out.println(paymentSuccess);
//        if (paymentSuccess != null) {
//            if (paymentSuccess) {
//                model.addAttribute("paymentSuccess", true);
//            }
//            else  {
//                model.addAttribute("paymentSuccess", false);
//            }
//            session.removeAttribute("paymentSuccess");
//        }

        // LEGAL DOCUMENT
        List<LegalDocument> legalDocuments = legalDocumentService.getAllLegalDocuments();
        legalDocuments.sort(Comparator.comparing(LegalDocument::getIssueDate).reversed());

        // Lấy 10 văn bản
        List<LegalDocument> newLegalDocuments = legalDocumentService.getLatestDocuments(); // Mới nhất
        List<LegalDocument> officialDocuments = legalDocumentService.getTop10OfficialDocuments(); // Công văn
        List<LegalDocument> circularDocuments = legalDocumentService.getTop10CircularDocuments(); // Thông tư
        List<LegalDocument> decisionDocuments = legalDocumentService.getTop10DecisionDocuments(); // Quyết định

//        Map<String, List<LegalDocument>> legalDocumentsByType = legalDocuments.stream()
//                .filter(doc -> doc.getLegalDocumentDetail() != null)
//                .collect(Collectors.groupingBy(doc -> doc.getLegalDocumentDetail().getDocumentType()));

        model.addAttribute("documents", legalDocuments);
        model.addAttribute("newDocuments", newLegalDocuments);
        model.addAttribute("officialDocuments", officialDocuments);
        model.addAttribute("circularDocuments", circularDocuments);
        model.addAttribute("decisionDocuments", decisionDocuments);

        HttpSession session = request.getSession();
        Boolean paymentSuccess = (Boolean) session.getAttribute("paymentSuccess");
        System.out.println(paymentSuccess);

        if (paymentSuccess != null) {
            if (paymentSuccess) {
                model.addAttribute("paymentSuccess", true);
            }
            else  {
                model.addAttribute("paymentSuccess", false);
            }
            session.removeAttribute("paymentSuccess");
        }

        return "TrangChu";
    }
}
