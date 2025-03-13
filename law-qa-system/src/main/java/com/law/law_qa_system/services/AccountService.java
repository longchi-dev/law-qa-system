package com.law.law_qa_system.services;

import com.law.law_qa_system.enums.EnumTypes;
import com.law.law_qa_system.models.Account;
import com.law.law_qa_system.models.ResponseObject;
import com.law.law_qa_system.models.User;
import com.law.law_qa_system.models.VerificationToken;
import com.law.law_qa_system.repositories.AccountRepository;
import com.law.law_qa_system.repositories.UserRepository;
import com.law.law_qa_system.repositories.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;

    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    public String login(Account account) {
        System.out.println("Email: " + account.getEmail());
        System.out.println("Password: " + account.getPassword());

        if ("admin@gmail.com".equals(account.getEmail()) && "admin".equals(account.getPassword())) {
            return "admin";
        }

        Optional<Account> foundAccount = accountRepository.findByEmail(account.getEmail());
        if (foundAccount.isEmpty()) {
            return "Email hoặc mật khẩu không đúng";
        }

        Account existingAccount = foundAccount.get();
        if (!passwordEncoder.matches(account.getPassword(), existingAccount.getPassword())) {
            return "Email hoặc mật khẩu không đúng";
        }

        return "user";
    }


    public String register(Account account) {
        Optional<Account> foundAccount = accountRepository.findByEmail(account.getEmail());
        if (foundAccount.isPresent()) {
            return "Email đã tồn tại";
        }

        if(account.getEmail().equals("admin@gmail.com")) {
            return "Email đã được sử dụng";
        }

        // Mã hóa mật khẩu trước khi lưu
      // account.setPassword(passwordEncoder.encode(account.getPassword()));
        String encodedPassword = passwordEncoder.encode(account.getPassword());
        if (account.getRole() == null) {
            account.setRole(EnumTypes.AccountRole.USER);
        }

        if (account.getStatus() == null) {
            account.setStatus(EnumTypes.AccountStatus.ACTIVE);
        }

        if(account.getAuthProvider() == null) {
            account.setAuthProvider(EnumTypes.AuthProvider.LOCAL);
        }

     //   Account savedAccount = accountRepository.save(account);
     //   logger.info("Account registered successfully: " + account.getEmail());

  //      User user = new User();
      //  user.setAccount(savedAccount);
     //   user.setName("User" + account.getId());
     //   userRepository.save(user);
     //   verificationService.sendVerificationEmail(user);
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setEmail(account.getEmail());  // Chỉ lưu email, chưa lưu account
        verificationToken.setEncodedPassword(account.getPassword());
        verificationToken.setExpiryDate(Instant.now().plus(5, ChronoUnit.MINUTES));

        tokenRepository.save(verificationToken);
        String confirmLink = "http://localhost:8080/account/verify?token=" + token;
        emailService.sendEmail(account.getEmail(), "Xác nhận tài khoản",
                "Nhấn vào link để xác nhận: " + confirmLink);

        return null;
    }

    public String changePassword(Account account, String currentPassword, String newPassword, String confirmPassword) {
        if (!passwordEncoder.matches(currentPassword, account.getPassword())) {
            return "Mật khẩu cũ không chính xác!";
        }

        if (!newPassword.equals(confirmPassword)) {
            return "Mật khẩu mới và xác nhận không khớp!";
        }

        if (newPassword.length() < 6) {
            return "Mật khẩu mới phải dài ít nhất 6 ký tự!";
        }

        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);

        return null;
    }

    public Account getAccountByEmail(String email) {
        return accountRepository.findByEmail(email)
                .orElse(null);
    }
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }
}

