package com.law.law_qa_system.services;

import com.law.law_qa_system.models.SubscriptionPackage;
import com.law.law_qa_system.models.SubscriptionPlan;
import com.law.law_qa_system.models.User;
import com.law.law_qa_system.repositories.SubcriptionPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionPlanService {
    @Autowired
    private SubcriptionPlanRepository subcriptionPlanRepository;
    @Autowired
    private UserService userService;
    public boolean purchaseSubscription(User user, Integer tokens, Double price, String packageName, LocalDateTime now) {
        if (!userService.hasSufficientBalance(user, price)) {
            return false;
        }
        userService.deductBalance(user, price);
        SubscriptionPlan newPlan = new SubscriptionPlan(user, tokens, price, packageName, now);
        subcriptionPlanRepository.save(newPlan);
        return true; // Mua thành công
    }
    public Optional<SubscriptionPlan> getActiveSubscription(Long userId) {
        return subcriptionPlanRepository.findTopByUserIdOrderByTokenDesc(userId);
    }


    public SubscriptionPackage getPackageByName(String packageName) {
        switch (packageName) {
            case "Standard":
                return new SubscriptionPackage("Standard", 1000000, "500000 VNĐ");
            case "Premium":
                return new SubscriptionPackage("Premium", 5000000, "2000000 VNĐ");
            case "Enterprise":
                return new SubscriptionPackage("Enterprise", 10000000, "3000000 VNĐ");
            default:
                throw new IllegalArgumentException("Gói không hợp lệ");
        }
    }
    public List<SubscriptionPlan> getAllSubscriptionsByUser(Long userId) {
        // Truy vấn tất cả các gói đăng ký của người dùng từ cơ sở dữ liệu
        return subcriptionPlanRepository.findByUserId(userId);
    }
}

