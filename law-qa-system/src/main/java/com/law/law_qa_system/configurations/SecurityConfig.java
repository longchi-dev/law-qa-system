package com.law.law_qa_system.configurations;

import com.law.law_qa_system.models.Account;
import com.law.law_qa_system.repositories.AccountRepository;
import com.law.law_qa_system.services.custom.CustomOAuth2UserService;
import com.law.law_qa_system.services.custom.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
public class SecurityConfig {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    @Lazy
    private CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.addAllowedOrigin("*");
                    config.addAllowedMethod("*");
                    config.addAllowedHeader("*");
                    return config;
                }))
                .authenticationProvider(daoAuthenticationProvider())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/chat/send").permitAll()
                        .requestMatchers("/static/**", "/css/**", "/images/**", "/js/**", "/PDF/**", "/word/**").permitAll()
                        .requestMatchers("/account/login", "/account/register", "/chat", "/account/change-password", "/dashboard" ) .permitAll()
                        .requestMatchers("/admin/**").access((authentication, context) -> {
                            HttpServletRequest request = (HttpServletRequest) context.getRequest();
                            HttpSession session = request.getSession(false);
                            String role = (session != null) ? (String) session.getAttribute("role") : null;
                            return "ROLE_ADMIN".equals(role) ? new AuthorizationDecision(true) : new AuthorizationDecision(false);
                        })
                        .requestMatchers("/user/**").hasRole("USER")
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/?error")
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/?error")
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                )
                .logout(logout -> logout
                        .logoutUrl("/account/dang-xuat")
                        .logoutSuccessUrl("/")
                        .permitAll()
                )
                .sessionManagement(session -> session
                        .sessionFixation().migrateSession()
                )
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                )
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
