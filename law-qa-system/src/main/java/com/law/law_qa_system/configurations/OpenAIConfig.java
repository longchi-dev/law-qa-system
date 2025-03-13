package com.law.law_qa_system.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAIConfig {
    @Value("${openai.api.key}")
    private String apiKey;

    public String getApiKey() {
        return apiKey;
    }
}
