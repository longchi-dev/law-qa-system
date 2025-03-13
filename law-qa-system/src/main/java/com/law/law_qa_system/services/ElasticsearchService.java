package com.law.law_qa_system.services;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.KnnSearch;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.HighlightField;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.law.law_qa_system.models.DocumentDetailEmbeddingES;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class ElasticsearchService {
    @Autowired
    private ElasticsearchClient client;
    @Autowired
    private OllamaEmbeddingService ollamaEmbeddingService;
    @Autowired
    private OpenAIService openAIService;
    @Autowired
    private ChatDetailService chatDetailService;
    @Autowired
    private ObjectMapper objectMapper;
    public List<String> searchKnn(String query) throws IOException {
        String answer = openAIService.generateAnswer(query);
        JsonNode rootNode = objectMapper.readTree(answer);
        String content = rootNode.path("choices").get(0).path("message").path("content").asText();
        content = content.replaceAll("(?s)```json|```", "").trim();
        JsonNode jsonNode = objectMapper.readTree(content);
        List<String> keywords = objectMapper.convertValue(jsonNode.get("keywords"), new TypeReference<List<String>>() {});
        System.out.println("Từ khóa trích xuất: " + keywords);
        List<Double> queryEmbedding = ollamaEmbeddingService.getEmbedding(query);
        List<Float> queryEmbeddingFloat = queryEmbedding.stream()
                .map(Double::floatValue)
                .toList();

        KnnSearch knnSearch = KnnSearch.of(q -> q
                .field("embedding") // Trường chứa vector embedding
                .k(20)

                .queryVector(queryEmbeddingFloat) // Vector truy vấn
        );
        KnnSearch knnTitle = KnnSearch.of(q -> q
                .field("embedding_title") // Trường chứa embedding của title
                .k(10)
                .queryVector(queryEmbeddingFloat)
        );
        Query fulltextQuery = Query.of(q -> q
                .bool(b -> b
                        .should(keywords.stream()
                                .map(keyword -> Query.of(q2 -> q2
                                        .match(m -> m
                                                .field("title")
                                                .query(keyword)
                                                .boost(3.0F) // Ưu tiên cao hơn cho title
                                        )
                                ))
                                .toList()
                        )
                        .should(keywords.stream()
                                .map(keyword -> Query.of(q2 -> q2
                                        .match(m -> m
                                                .field("chunkText")
                                                .query(keyword)
                                        )
                                ))
                                .toList()
                        )
                        .minimumShouldMatch("1")
                )
        );

        SearchRequest request = new SearchRequest.Builder()
                .index("document_embedding") // Chỉ mục Elasticsearch
                .knn(knnSearch)
                .knn(knnTitle)
                .query(fulltextQuery)// Truy vấn KNN
                .sort(s -> s
                        .field(f -> f
                                .field("_score") // Sắp xếp theo độ tương đồng (score)
                        )
                )
                .build();


        // Gửi yêu cầu và nhận kết quả tìm kiếm
        SearchResponse<Map> response = client.search(request, Map.class);
        response.hits().hits().forEach(hit -> {
            System.out.println("Hit source: " + hit.source());
            System.out.println("Hit score: " + hit.score());
        });
        System.out.println("Raw Response: " + response.toString());
        // Trả về kết quả
        List<String> results = new ArrayList<>();
        for (Hit<Map> hit : response.hits().hits()) {
            Map source = hit.source();
            if (source != null && source.containsKey("chunkText")) {
                Object content2 = source.get("chunkText");
                if (content2 instanceof String) {
                    results.add((String) content2);
                } else {
                    System.out.println("chunkText không phải String: " + content2.getClass());
                }
            } else {
                System.out.println("Không tìm thấy 'chunkText' trong kết quả: " + source);
            }

        }
        return results;
    }

    public String searchLegalDocuments(String userQuery) throws IOException {
        List<String> chunks = searchKnn(userQuery);
        System.out.println(chunks);
        if (chunks.isEmpty()) {
            return "Không tìm thấy văn bản nào phù hợp với câu hỏi của bạn.";
        }
        if (chunks.isEmpty()) {
            return "Không tìm thấy văn bản nào phù hợp với câu hỏi của bạn.";
        }
        String relevantText = openAIService.rerankChunks(userQuery, chunks);
        return relevantText;
    }

    public List<Map<String, Object>> searchDocuments(String query) throws IOException {
        String answer = chatDetailService.generateAnswer(query);
        System.out.println(answer);
        JsonNode rootNode = objectMapper.readTree(answer);
        String content = rootNode.path("choices").get(0).path("message").path("content").asText();
        content = content.replaceAll("(?s)```json|```", "").trim();

        List<String> keywords =  Arrays.asList(objectMapper.readValue(content, String[].class));

        System.out.println("Từ khóa trích xuất: " + keywords);

        List<Double> embeddingVector = ollamaEmbeddingService.getEmbedding(query);
        List<Float> queryEmbeddingFloat = embeddingVector.stream()
                .map(Double::floatValue)
                .toList();

        SearchRequest request = new SearchRequest.Builder()
                .index("document_embedding")
                .knn(knnQuery -> knnQuery
                        .field("embedding")
                        .queryVector(queryEmbeddingFloat)
                        .numCandidates(100)
                        .k(20)
                )
                .query(q -> q
                        .bool(b -> b
                                .should(m -> m
                                        .multiMatch(mm -> mm
                                                .fields(Arrays.asList("chunkText", "title"))
                                                .query(String.join(" ", keywords))
                                        )
                                )
                        )
                )
                .highlight(h -> h
                        .fields("chunkText", new HighlightField.Builder().build())
                        .preTags("<b style='background-color: yellow;'>")
                        .postTags("</b>")
                )
                .build();

        SearchResponse<DocumentDetailEmbeddingES> response = client.search(request, DocumentDetailEmbeddingES.class);
        List<Map<String, Object>> results = new ArrayList<>();
        for (Hit<DocumentDetailEmbeddingES> hit : response.hits().hits()) {
            Map result = objectMapper.convertValue(hit.source(), Map.class);
            result.put("highlight", hit.highlight().get("chunkText"));
            results.add(result);
        } Map<String, Map<String, Object>> groupedResults = new HashMap<>();
        for (Hit<DocumentDetailEmbeddingES> hit : response.hits().hits()) {
            Map result = objectMapper.convertValue(hit.source(), Map.class);
            Object documentIdObj  =  result.get("documentId");
            String documentId;
            if (documentIdObj instanceof String) {
                documentId = (String) documentIdObj;
            } else if (documentIdObj instanceof Number) {
                documentId = documentIdObj.toString();
            } else {
                documentId = "UNKNOWN"; // Giá trị mặc định nếu không xác định được
            }
            // Lấy danh sách highlight
            List<String> highlights = hit.highlight() != null ? hit.highlight().get("chunkText") : Collections.emptyList();

            // Nếu documentId đã có trong groupedResults, gộp highlight lại
            if (groupedResults.containsKey(documentId)) {
                Map<String, Object> existingResult = groupedResults.get(documentId);

                // Gộp highlight từ nhiều chunk khác nhau
                List<String> existingHighlights = (List<String>) existingResult.get("highlight");
                if (existingHighlights == null) {
                    existingHighlights = new ArrayList<>();  // Nếu chưa có highlight, khởi tạo danh sách rỗng
                }
                if (highlights != null) {
                    existingHighlights.addAll(highlights);  // Thêm highlight nếu highlights không phải null
                }
                existingResult.put("highlight", existingHighlights);  // Cập nhật lại giá trị "highlight"
            } else {
                // Nếu chưa có documentId này, thêm vào map
                List<String> safeHighlights = (highlights != null) ? new ArrayList<>(highlights) : new ArrayList<>();  // Đảm bảo highlights không phải null
                result.put("highlight", safeHighlights);
                groupedResults.put(documentId, result);
            }
        }
        return new ArrayList<>(groupedResults.values());
    }


}
