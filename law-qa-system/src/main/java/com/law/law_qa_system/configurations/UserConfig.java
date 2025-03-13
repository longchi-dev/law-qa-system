package com.law.law_qa_system.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class UserConfig {
    @Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager() {
        UserDetails chilong = User
                .withUsername("chilong")
                .password("{noop}123456")
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(chilong);
    }
}
