package com.law.law_qa_system.controllers;

import com.law.law_qa_system.models.SubscriptionPackage;
import com.law.law_qa_system.models.User;
import com.law.law_qa_system.services.SubscriptionPlanService;
import com.law.law_qa_system.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/subscription")
public class SubscriptionPlanController {
    @Autowired
    private SubscriptionPlanService subscriptionPlanService;
    @Autowired
    private UserService userService;
    @GetMapping("/")
    public String purchaseSubscription (Principal principal, Model model) {
        String email = principal.getName();
        User user = userService.getUserByEmail(email);
        model.addAttribute("user", user);
        return "GoiDichVu";
    }
    @PostMapping("purchase")
    public String purchaseSubscription(@RequestParam String packageName, Principal principal, Model model) {
        String email = principal.getName();
        User user = userService.getUserByEmail(email);
        model.addAttribute("user", user);

        SubscriptionPackage selectedPackage = subscriptionPlanService.getPackageByName(packageName);
        Double price = Double.parseDouble(selectedPackage.getPrice().replace(" VNĐ", "").replace(",", ""));
        int tokens = selectedPackage.getTokenAmount();
        if (!userService.hasSufficientBalance(user, price)) {
            model.addAttribute("message", "Không đủ số dư để mua gói dịch vụ.");
            return "TrangChu"; // Trả về trang lỗi nếu không đủ tiền
        }
        boolean success = subscriptionPlanService.purchaseSubscription(user, tokens, price, packageName, LocalDateTime.now());
        if (success) {

            user.setTokens(user.getTokens()+tokens);
            userService.updateUser(user);
            model.addAttribute("message", "Mua gói dịch vụ thành công!");
            model.addAttribute("selectedPackage", selectedPackage);
        } else {
            model.addAttribute("message", "Đã có lỗi xảy ra trong quá trình mua gói.");
        }
        return "TrangChu";
    }

}
