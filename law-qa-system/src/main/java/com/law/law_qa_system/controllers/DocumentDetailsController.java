package com.law.law_qa_system.controllers;

import com.law.law_qa_system.models.DocumentDetails;
import com.law.law_qa_system.models.User;
import com.law.law_qa_system.services.DocumentDetailsService;
import com.law.law_qa_system.services.SavedDocumentService;
import com.law.law_qa_system.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/bai-viet")
public class DocumentDetailsController {

    @Autowired
    private DocumentDetailsService documentDetailsService;
    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String getAllDocumentDetails(Model model, Principal principal) {
        List<DocumentDetails> documentDetailsList = documentDetailsService.getAllDocumentDetails();
        model.addAttribute("documentDetailsList", documentDetailsList);
        if (principal != null) {
            String email = principal.getName();
            User user = userService.getUserByEmail(email); // Lấy thông tin User từ DB
            model.addAttribute("user", user); // Truyền User vào Model
        } else {
            model.addAttribute("user", null); // Nếu không có Principal, gán null
        }
        return "BaiViet";
    }


    @GetMapping("/tao-bai-viet")
    public String showCreateForm(Model model) {
        model.addAttribute("documentDetails", new DocumentDetails());
        return "documentDetails/create";
    }

    @PostMapping("/")
    public String createDocumentDetails(@ModelAttribute DocumentDetails documentDetails, Model model) {
        documentDetailsService.createDocumentDetails(documentDetails);
        return "redirect:/document-details/";
    }

    @GetMapping("/chinh-bai-viet/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        DocumentDetails documentDetails = documentDetailsService.getDocumentDetailsById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid document details ID: " + id));
        model.addAttribute("documentDetails", documentDetails);
        return "documentDetails/edit";
    }

    @PostMapping("/chinh-bai-viet/{id}")
    public String updateDocumentDetails(@PathVariable Long id, @ModelAttribute DocumentDetails documentDetails, Model model) {
        documentDetailsService.updateDocumentDetails(id, documentDetails);
        return "redirect:/document-details/";
    }

    @GetMapping("/xoa-bai-viet/{id}")
    public String deleteDocumentDetails(@PathVariable Long id) {
        documentDetailsService.deleteDocumentDetails(id);
        return "redirect:/document-details/";
    }

    @GetMapping("/{id}")
    public String getDocumentDetailsById(@PathVariable Long id, Model model) {
        DocumentDetails documentDetails = documentDetailsService.getDocumentDetailsById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid document details ID: " + id));
        model.addAttribute("documentDetails", documentDetails);
        return "documentDetails/detail";
    }

    @GetMapping("/bai-viet/{documentId}")
    public String findByDocumentId(@PathVariable Long documentId, Model model) {
        List<DocumentDetails> documentDetailsList = documentDetailsService.getDocumentDetailsByDocumentId(documentId);
        model.addAttribute("documentDetailsList", documentDetailsList);
        return "documentDetails/list";
    }

    @GetMapping("/agency/{agency}")
    public String findByAgency(@PathVariable String agency, Model model) {
        List<DocumentDetails> documentDetailsList = documentDetailsService.getDocumentDetailsByAgency(agency);
        model.addAttribute("documentDetailsList", documentDetailsList);
        return "documentDetails/list";
    }

    @GetMapping("/gazette/{gazetteNumber}")
    public String findByGazetteNumber(@PathVariable String gazetteNumber, Model model) {
        List<DocumentDetails> documentDetailsList = documentDetailsService.getDocumentDetailsByGazetteNumber(gazetteNumber);
        model.addAttribute("documentDetailsList", documentDetailsList);
        return "documentDetails/list";
    }

    @GetMapping("/validity/{validityStatus}")
    public String findByValidityStatus(@PathVariable String validityStatus, Model model) {
        List<DocumentDetails> documentDetailsList = documentDetailsService.getDocumentDetailsByValidityStatus(validityStatus);
        model.addAttribute("documentDetailsList", documentDetailsList);
        return "documentDetails/list";
    }

    @GetMapping("/field/{field}")
    public String findByField(@PathVariable String field, Model model) {
        List<DocumentDetails> documentDetailsList = documentDetailsService.getDocumentDetailsByField(field);
        model.addAttribute("documentDetailsList", documentDetailsList);
        return "documentDetails/list";
    }
}
