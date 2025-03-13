package com.law.law_qa_system.services;

import com.law.law_qa_system.models.Account;
import com.law.law_qa_system.models.User;
import com.law.law_qa_system.models.VerificationToken;
import com.law.law_qa_system.repositories.UserRepository;
import com.law.law_qa_system.repositories.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailService {
    @Autowired
    private VerificationTokenRepository tokenRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom("your_email@gmail.com");
        mailSender.send(message);
    }
}
