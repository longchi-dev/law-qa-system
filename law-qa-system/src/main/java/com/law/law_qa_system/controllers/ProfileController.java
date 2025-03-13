package com.law.law_qa_system.controllers;

import com.law.law_qa_system.models.Document;
import com.law.law_qa_system.models.LegalDocument;
import com.law.law_qa_system.models.SubscriptionPlan;
import com.law.law_qa_system.models.User;
import com.law.law_qa_system.services.SavedDocumentService;
import com.law.law_qa_system.services.SubscriptionPlanService;
import com.law.law_qa_system.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class ProfileController {
    @Autowired
    private UserService userService;
    @Autowired
    private SavedDocumentService savedDocumentService;
    @Autowired
    private SubscriptionPlanService subscriptionPlanService;

    @GetMapping("/thong-tin-tai-khoan")
    public String showProfile(Model model, Principal principal) {
        String email = principal.getName();
        User user = userService.getUserByEmail(email);

        if (user == null) {
            return "TrangChu";
        }

        Optional<SubscriptionPlan> subscriptionOpt = subscriptionPlanService.getActiveSubscription(user.getId());
        if (subscriptionOpt.isPresent()) {
            SubscriptionPlan subscription = subscriptionOpt.get();
            model.addAttribute("packageName", subscription.getToken());
        } else {
            model.addAttribute("packageName", "Chưa mua token");
        }

        List<SubscriptionPlan> subscriptions = subscriptionPlanService.getAllSubscriptionsByUser(user.getId());
        model.addAttribute("subscriptions", subscriptions);
        model.addAttribute("user", user);

        List<LegalDocument> savedDocuments = savedDocumentService.getSavedDocuments(user.getId());
        model.addAttribute("documents", savedDocuments);
        return "TrangCaNhan";
    }


    @PostMapping("/update")
    public String updateUser(@ModelAttribute User updateUser) {

        String result = userService.updateUser(updateUser);
        if (result != null) {
            return "redirect:/user/thong-tin-tai-khoan?error=" + result;
        }
        return "redirect:/user/thong-tin-tai-khoan" ;
    }
}
