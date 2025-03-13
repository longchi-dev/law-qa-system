package com.law.law_qa_system.controllers;

import com.law.law_qa_system.dto.PaymentDTO;
import com.law.law_qa_system.response.ResponseObject;
import com.law.law_qa_system.models.Account;
import com.law.law_qa_system.models.User;
import com.law.law_qa_system.repositories.UserRepository;
import com.law.law_qa_system.services.PaymentService;
import com.law.law_qa_system.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("${spring.application.api-prefix}/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final UserRepository userRepository;
    private final PaymentService paymentService;
    @Autowired
    private UserService userService;
    @GetMapping("/")
    public String paymentPage() {
        return "payment"; // Giao diện nhập số tiền và mã ngân hàng
    }
    @GetMapping("/vn-pay")
    public ResponseObject<PaymentDTO.VNPayResponse> pay(HttpServletRequest request, Principal principal) {
        PaymentDTO.VNPayResponse response = paymentService.createVnPayPayment(request, principal);
        return new ResponseObject<>(HttpStatus.OK, "Success", response);
    }
    @GetMapping("/vn-pay-callback")
    public String payCallbackHandler(HttpServletRequest request, Principal principal, HttpSession session) {
        String email = principal.getName();
        User user = userService.getUserByEmail(email); // Lấy thông tin User từ DB
        String status = request.getParameter("vnp_ResponseCode");
        Long amount = Long.parseLong(request.getParameter("vnp_Amount")) / 100; // VNPay gửi amount * 100
        if (status.equals("00")) {
            paymentService.updateUserBalance(user, amount);
            session.setAttribute("paymentSuccess", true);
            System.out.println("session " + session.getAttribute("paymentSuccess"));
            return "redirect:/";
        } else {
            session.setAttribute("paymentSuccess", false);
            return "redirect:/";
        }
    }

    @GetMapping("/payment-result")
    public String paymentResult(Model model) {
        // Dữ liệu trả về từ VNPAY (giả sử bạn nhận được từ API VNPAY)
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("code", 200);
        responseData.put("message", "Success");

        Map<String, String> data = new HashMap<>();
        data.put("code", "00");
        data.put("message", "Success");
        data.put("paymentUrl", "");

        responseData.put("data", data);

        // Truyền dữ liệu vào model để hiển thị trong Thymeleaf
        model.addAttribute("response", responseData);

        return "paymentResult"; // Tên của trang Thymeleaf mà bạn muốn hiển thị
    }

}
