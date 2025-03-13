package com.law.law_qa_system.controllers;

import com.law.law_qa_system.models.User;
import com.law.law_qa_system.services.SubscriptionPlanService;
import com.law.law_qa_system.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/goi-cuoc")
public class PackageController {

    @Autowired
    private UserService userService;

    @Autowired
    private SubscriptionPlanService subscriptionPlanService;

    @GetMapping("")
    public String paymentPage(Principal principal, Model model) {
        if (principal == null) {
            model.addAttribute("user", null);
            return "redirect:/";
        }

        String email = principal.getName();
        User user = userService.getUserByEmail(email);
        if (user == null) {
            return "redirect:/";
        }

        model.addAttribute("user", user);


        return "redirect:/chat";
    }
}

