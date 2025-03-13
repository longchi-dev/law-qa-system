package com.law.law_qa_system.controllers;

import com.law.law_qa_system.enums.EnumTypes;
import com.law.law_qa_system.models.Account;
import com.law.law_qa_system.models.User;
import com.law.law_qa_system.models.VerificationToken;
import com.law.law_qa_system.repositories.AccountRepository;
import com.law.law_qa_system.repositories.UserRepository;
import com.law.law_qa_system.repositories.VerificationTokenRepository;
import com.law.law_qa_system.services.AccountService;
import com.law.law_qa_system.services.EmailService;
import com.law.law_qa_system.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/account")
public class AuthController {
    @Autowired
    AccountService accountService;
    @Autowired
    EmailService emailService;
    @Autowired
    VerificationTokenRepository tokenRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/dang-nhap")
    public String showLoginPage() {
        return "TrangChu";
    }

    @GetMapping("/dang-ky")
    public String showRegisterPage() {
        return "auth/register";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute Account account, HttpSession session, RedirectAttributes redirectAttributes) {
        String loginResult = accountService.login(account);
        System.out.println("Login Result: " + loginResult);
        if ("admin".equals(loginResult)) {
            session.setAttribute("role", "ROLE_ADMIN");
            return "redirect:/admin/tai-khoan";
        }
        else if ("user".equals(loginResult)) {
            session.setAttribute("loggedInUser", account);
            return "redirect:/";
        }
        else {
            redirectAttributes.addFlashAttribute("error", loginResult);
            return "redirect:/";
        }
    }

    @PostMapping("/dang-ky")
    public String register(@ModelAttribute Account account, Model model) {
        String error = accountService.register(account);

        if (error != null) {
            model.addAttribute("error", error);
            return "auth/register";
        }

        model.addAttribute("message", "Gửi email thành công vui lòng xác nhận đường link trong email đăng ký trong vòng 5 phút");
        return "auth/login";
    }

    @GetMapping("/dang-xuat")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:dashboard";
    }

    @GetMapping("/doi-mat-khau")
    public String showChangePasswordPage() {
        return "auth/change-password";
    }

    @PostMapping("/doi-mat-khau")
    public String changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            HttpSession session,
            Model model
    ) {
        Account loggedInUser = (Account) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "TrangChu";
        }

        String error = accountService.changePassword(loggedInUser, currentPassword, newPassword, confirmPassword);
        if (error != null) {
            model.addAttribute("error", error);
            return "auth/change-password";
        }

        model.addAttribute("message", "Đổi mật khẩu thành công!");
        return "TrangChu";
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyAccount(@RequestParam String token) {
        Optional<VerificationToken> optionalToken = tokenRepository.findByToken(token);

        if (optionalToken.isEmpty()) {
            return ResponseEntity.badRequest().body("Token không hợp lệ.");
        }

        VerificationToken verificationToken = optionalToken.get();
        if (verificationToken.isExpired()) {
            return ResponseEntity.badRequest().body("Token đã hết hạn.");
        }

        Account account = new Account();
        account.setEmail(verificationToken.getEmail());
        account.setPassword(verificationToken.getEncodedPassword()); // Lấy password đã mã hóa
        account.setRole(EnumTypes.AccountRole.USER);
        account.setStatus(EnumTypes.AccountStatus.ACTIVE);
        account.setAuthProvider(EnumTypes.AuthProvider.LOCAL);

        Account savedAccount = accountRepository.save(account);
        User user = new User();
        user.setAccount(savedAccount);
        user.setName("User" + savedAccount.getId());

        userRepository.save(user);
        // Xóa token sau khi xác nhận

        tokenRepository.delete(verificationToken);
        String successMessage = "Chúc mừng bạn đã đăng ký thành công! Bạn có thể đăng nhập tại: http://localhost:8080/account/dang-nhap";
        emailService.sendEmail(account.getEmail(), "Đăng ký thành công", successMessage);

        // **Chuyển hướng về trang chủ**
        return ResponseEntity.ok("Xác nhận thành công! Vui lòng <a href='http://localhost:8080/account/dang-nhap'>đăng nhập</a>.");
    }
}
