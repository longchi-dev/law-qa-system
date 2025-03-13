package com.law.law_qa_system.services.custom;

import com.law.law_qa_system.models.Account;
import com.law.law_qa_system.repositories.AccountRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private HttpSession session;

    public CustomUserDetailsService() {
        System.out.println("CustomUserDetailsService is initialized.");
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email not found: " + email));

        session.setAttribute("loggedInUser", account);
        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + account.getRole().name())
        );

        System.out.println("Authorities for " + account.getEmail() + ": ROLE_" + account.getRole().name());

        return new org.springframework.security.core.userdetails.User(
                account.getEmail(),
                account.getPassword(),
                authorities
        );
    }
}
