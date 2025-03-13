package com.law.law_qa_system.services;

import com.law.law_qa_system.dto.PaymentDTO;
import com.law.law_qa_system.configurations.VNPAYConfig;
import com.law.law_qa_system.models.Account;
import com.law.law_qa_system.models.Transaction;
import com.law.law_qa_system.models.User;
import com.law.law_qa_system.repositories.TransactionRepository;
import com.law.law_qa_system.repositories.UserRepository;
import com.law.law_qa_system.util.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final VNPAYConfig vnPayConfig;
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    public PaymentDTO.VNPayResponse createVnPayPayment(HttpServletRequest request, Principal principal) {
        long amount = Integer.parseInt(request.getParameter("amount")) * 100L;
        String bankCode = request.getParameter("bankCode");
        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig();
        vnpParamsMap.put("vnp_Amount", String.valueOf(amount));
        if (bankCode != null && !bankCode.isEmpty()) {
            vnpParamsMap.put("vnp_BankCode", bankCode);
        }
        vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));
        //build query url
        String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
        String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;
        String email = principal.getName();
        User user = userService.getUserByEmail(email); // Lấy thông tin User từ DB
        createTransaction(user, amount, bankCode, "PENDING");

        return PaymentDTO.VNPayResponse.builder()
                .code("ok")
                .message("success")
                .paymentUrl(paymentUrl).build();

    }

    public void createTransaction(User user, Long amount, String bankCode, String status) {
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setAmount(amount);
        transaction.setBankCode(bankCode);
        transaction.setStatus(status);
        transactionRepository.save(transaction);
    }

    @Transactional
    public void updateUserBalance(User user, Long amount) {
        user.setBalance(user.getBalance() + amount);
        userRepository.save(user);
    }
}
