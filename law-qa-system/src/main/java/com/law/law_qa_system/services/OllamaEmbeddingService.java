package com.law.law_qa_system.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.ArrayList;
import java.util.List;

@Service
public class OllamaEmbeddingService {
    @Value("${ollama.api.url}")
    private String ollamaApiUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public List<Double> getEmbedding(String text) {
        try {
            String payload = String.format("{\"model\": \"nomic-embed-text\", \"prompt\": \"%s\"}", text);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(payload, headers);
            ResponseEntity<String> response = restTemplate.exchange(ollamaApiUrl, HttpMethod.POST, entity, String.class);

            return parseEmbedding(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private List<Double> parseEmbedding(String responseBody) {
        List<Double> embedding = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode embeddingNode = root.path("embedding");

            for (JsonNode value : embeddingNode) {
                embedding.add(value.asDouble());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return embedding;
    }
}
