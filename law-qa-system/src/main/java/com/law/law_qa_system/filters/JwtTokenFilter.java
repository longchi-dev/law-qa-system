//package com.law.law_qa_system.filters;
//
//import com.law.law_qa_system.components.JwtTokenUtils;
//import com.law.law_qa_system.models.Account;
//import com.law.law_qa_system.services.AccountService;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.NonNull;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.List;
//import java.util.AbstractMap.SimpleEntry;
//
//@Component
//@RequiredArgsConstructor
//public class JwtTokenFilter extends OncePerRequestFilter {
//
//    @Value("${api.prefix}")
//    private String apiPrefix;
//
//    private final JwtTokenUtils jwtTokenUtils;
//    private final AccountService accountService;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain)
//            throws ServletException, IOException {
//        try {
//            if (isByPassToken(request)) {
//                filterChain.doFilter(request, response);
//                return;
//            }
//            final String authHeader = request.getHeader("Authorization");
//            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header");
//                return;
//            }
//            final String token = authHeader.substring(7);
//            final String email = jwtTokenUtils.extractEmail(token);
//
//            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//                Account account = accountService.findByEmail(email);
//                if (account != null) {
//                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
//                            account,
//                            null,
//                            Arrays.asList(new SimpleGrantedAuthority(account.getRole().name()))
//                    );
//                    SecurityContextHolder.getContext().setAuthentication(authentication);
//                } else {
//                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Account not found");
//                    return;
//                }
//            }
//        } catch (Exception e) {
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: " + e.getMessage());
//        }
//        filterChain.doFilter(request, response);
//    }
//
//    private boolean isByPassToken(@NonNull HttpServletRequest request) {
//        final List<SimpleEntry<String, String>> bypassTokens = Arrays.asList(
//                new SimpleEntry<>(String.format("%s/account/login", apiPrefix), "POST"),
//                new SimpleEntry<>(String.format("%s/account/register", apiPrefix), "POST")
//        );
//
//        String requestPath = request.getServletPath();
//        String requestMethod = request.getMethod();
//        System.out.println("Request Path: " + requestPath + " Method: " + requestMethod);
//
//        for (SimpleEntry<String, String> entry : bypassTokens) {
//            if (entry.getKey().equals(requestPath) && entry.getValue().equals(requestMethod)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//}
