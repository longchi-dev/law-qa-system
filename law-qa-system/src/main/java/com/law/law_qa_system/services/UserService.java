package com.law.law_qa_system.services;

import com.law.law_qa_system.models.Account;
import com.law.law_qa_system.models.SubscriptionPlan;
import com.law.law_qa_system.models.User;
import com.law.law_qa_system.repositories.AccountRepository;
import com.law.law_qa_system.repositories.SubcriptionPlanRepository;
import com.law.law_qa_system.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private SubcriptionPlanRepository subcriptionPlanRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.orElse(null);
    }

    public User getUserByEmail(String email) {
        Optional<Account> account = accountRepository.findByEmail(email);

        if (account.isEmpty()) {
            throw new RuntimeException("Không tìm thấy tài khoản với email: " + email);
        }

        return userRepository.findByAccountId(account.get().getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin người dùng"));
    }

    public String updateUser(User updateUser) {
        if (updateUser.getAccount() == null || updateUser.getAccount().getEmail() == null) {
            return "Invalid Account";
        }

        Optional<Account> accountOptional = accountRepository.findByEmail(updateUser.getAccount().getEmail());
        if (accountOptional.isEmpty()) {
            return "Account Not Found";
        }

        Account account = accountOptional.get();
        Optional<User> userOptional = userRepository.findByAccount(account);

        if (userOptional.isEmpty()) {
            return "User Not Found";
        }

        User existUser = userOptional.get();
        if (updateUser.getName() != null) existUser.setName(updateUser.getName());
        if (updateUser.getAvatar() != null) existUser.setAvatar(updateUser.getAvatar());
        if (updateUser.getPhoneNum() != null) existUser.setPhoneNum(updateUser.getPhoneNum());
        if (updateUser.getBalance() != null) existUser.setBalance(updateUser.getBalance());
        if (updateUser.getCity() != null) existUser.setCity(updateUser.getCity());
        if (updateUser.getDistrict() != null) existUser.setDistrict(updateUser.getDistrict());
        if (updateUser.getWard() != null) existUser.setWard(updateUser.getWard());
        if (updateUser.getDateOfBirth() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate localDate = LocalDate.parse(updateUser.getDateOfBirth().toString(), formatter);
            existUser.setDateOfBirth(LocalDate.from(localDate.atStartOfDay()));
        }
        if (updateUser.getGender() != null) existUser.setGender(updateUser.getGender());

        userRepository.save(existUser);
        return null;
    }

    public String deleteUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            return "Không tìm thấy người dùng";
        }

        userRepository.delete(user.get());
        return null;
    }

    public Optional<User> getUserByAccountId(Long accountId) {
        return userRepository.findByAccountId(accountId); // Lấy User từ accountId
    }

    public boolean hasSufficientBalance(User user, Double amount) {
        return user.getBalance() >= amount;
    }
    public void deductBalance(User user, Double amount) {
        user.setBalance(user.getBalance() - amount);
        userRepository.save(user);
    }
}
