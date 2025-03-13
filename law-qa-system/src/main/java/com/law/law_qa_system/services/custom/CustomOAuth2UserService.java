package com.law.law_qa_system.services.custom;

import com.law.law_qa_system.enums.EnumTypes;
import com.law.law_qa_system.models.Account;
import com.law.law_qa_system.models.CustomOAuth2User;
import com.law.law_qa_system.models.User;
import com.law.law_qa_system.repositories.AccountRepository;
import com.law.law_qa_system.repositories.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private HttpSession session;


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getClientName();
        System.out.println(provider);

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        String avatar;
        if ("facebook".equalsIgnoreCase(provider)) {
            Map<String, Object> pictureMap = (Map<String, Object>) oAuth2User.getAttribute("picture");
            if (pictureMap != null) {
                Map<String, Object> data = (Map<String, Object>) pictureMap.get("data");
                if (data != null) {
                    avatar = (String) data.get("url");
                } else {
                    avatar = null;
                }
            } else {
                avatar = null;
            }
        } else {
            avatar = (String) oAuth2User.getAttribute("picture");
        }

        Account account = accountRepository.findByEmail(email).orElseGet(() -> {
            String generatedPassword = UUID.randomUUID().toString();
            String encodedPassword = passwordEncoder.encode(generatedPassword);

            Account newAccount = Account.builder()
                    .email(email)
                    .password(encodedPassword)
                    .role(EnumTypes.AccountRole.USER)
                    .status(EnumTypes.AccountStatus.ACTIVE)
                    .authProvider("facebook".equalsIgnoreCase(provider) ? EnumTypes.AuthProvider.FACEBOOK : EnumTypes.AuthProvider.GOOGLE) // Điều chỉnh authProvider
                    .build();
            return accountRepository.save(newAccount);
        });

        userRepository.findByAccountId(account.getId()).orElseGet(() -> {
            User user = User.builder()
                    .account(account)
                    .name(name)
                    .avatar(avatar)
                    .balance(0.0)
                    .build();
            return userRepository.save(user);
        });

        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + account.getRole().name())
        );

        session.setAttribute("loggedInUser", account);

        return new CustomOAuth2User(
                authorities,
                oAuth2User.getAttributes(),
                "email",
                email
        );
    }
}
