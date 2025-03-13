package com.law.law_qa_system.controllers;

import com.law.law_qa_system.api.ApiService;
import com.law.law_qa_system.crawl.LawCrawlerService;
import com.law.law_qa_system.models.*;
import com.law.law_qa_system.repositories.SubcriptionPlanRepository;
import com.law.law_qa_system.services.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.*;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AccountService accountService;
    @Autowired
    private UserService userService;
    @Autowired
    private DocumentService documentService;
    @Autowired
    private SubscriptionPlanService subscriptionPlanService;
//    @Autowired
//    private ApiService apiService;

    @Autowired
    private LegalDocumentService legalDocumentService;

    @Autowired
    private LawCrawlerService lawCrawlerService;

    @GetMapping("/tai-khoan")
    public String getInfoAccount(HttpServletRequest request, Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("currentURI", request.getRequestURI());
        model.addAttribute("users", users);
        return "admin/QuanLyTaiKhoan";
    }

    @GetMapping("/tai-lieu")
    public String getDocuments (HttpServletRequest request, Model model) {
//        List<Document> documents = documentService.getAllDocuments();
//        if (documents.isEmpty()) {
//            model.addAttribute("message", "No documents found.");
//        }
//        model.addAttribute("currentURI", request.getRequestURI());
//        model.addAttribute("documents", documents);
        List<LegalDocument> legalDocuments = legalDocumentService.getAllLegalDocuments();
        if (legalDocuments.isEmpty()) {
            model.addAttribute("message", "No documents found.");
        }
        model.addAttribute("currentURI", request.getRequestURI());
        model.addAttribute("documents", legalDocuments);
        return "admin/QuanLyTaiLieu";
    }

    @GetMapping("/doanh-thu")
    public String getRevenues(Model model) {
       // List<SubscriptionPlan> transactions = subscriptionPlanService.findAll();
        //double totalRevenue = transactions.stream()
        //        .mapToDouble(SubscriptionPlan::getAmount)
          //      .sum();

        model.addAttribute("transactions", "transactions");
        model.addAttribute("totalRevenue", "totalRevenue");
        return "admin/QuanLyDoanhThu";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id, Model model) {
        userService.deleteUser(id);
        return "redirect:/admin/tai-khoan";
    }

    @GetMapping("/dang-xuat")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:dashboard";
    }

//    @PostMapping("/fetch-documents")
//    public String fetchDocuments(RedirectAttributes redirectAttributes) {
//        try {
//            apiService.saveDocumentsToDatabase();
//            redirectAttributes.addFlashAttribute("message", "Tải văn bản thành công!");
//            redirectAttributes.addFlashAttribute("messageType", "success");
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("message", "Lỗi khi tải văn bản: " + e.getMessage());
//            redirectAttributes.addFlashAttribute("messageType", "error");
//        }
//        return "redirect:/admin/tai-lieu";
//    }

    @PostMapping("/fetch-documents")
    public String fetchDocuments(RedirectAttributes redirectAttributes) {
        try {
            lawCrawlerService.crawlLawList();
            redirectAttributes.addFlashAttribute("message", "Crawl văn bản thành công!");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Lỗi khi crawl văn bản: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
        }
        return "redirect:/admin/tai-lieu";
    }

    @PostMapping("/delete-document/{id}")
    public String deleteDocument(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            documentService.deleteDocument(id);
            redirectAttributes.addFlashAttribute("message", "Xóa tài liệu thành công!");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Lỗi khi xóa tài liệu: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
        }
        return "redirect:/admin/tai-lieu";
    }

}
