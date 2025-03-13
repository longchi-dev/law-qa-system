package com.law.law_qa_system.services;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.KnnSearch;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.law.law_qa_system.models.ChatDetailHistory;
import com.law.law_qa_system.models.ChatHistory;
import com.law.law_qa_system.models.User;
import com.law.law_qa_system.repositories.ChatDetailRepository;
import com.law.law_qa_system.repositories.ChatHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ChatService {

    @Autowired
    ChatDetailService chatDetailService;
    @Autowired
    private ChatHistoryRepository chatHistoryRepository;
    @Autowired
    private ChatDetailRepository chatDetailRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    OllamaEmbeddingService ollamaEmbeddingService;
    @Autowired
    private ElasticsearchClient client;
    public List<String> extractKeywords(String answer) {
        try {
            JsonNode rootNode = objectMapper.readTree(answer);
            String content = rootNode.path("choices").get(0).path("message").path("content").asText();
            System.out.println("Content: " + content);

            // Loại bỏ phần markdown code block (```) nếu có
            content = content.replaceAll("(?s)```json|```", "").trim();
            System.out.println("Cleaned Content: " + content);

            // Kiểm tra xem nội dung là object JSON hay mảng JSON
            if (content.startsWith("{")) {
                Map<String, Object> resultMap = objectMapper.readValue(content, Map.class);
                Object keywordsObj = resultMap.get("keywords");
                if (keywordsObj != null && keywordsObj instanceof List) {
                    return (List<String>) keywordsObj;
                }
            } else if (content.startsWith("[")) {
                // Nếu content là một mảng JSON
                return objectMapper.readValue(content, List.class);
            }

            System.out.println("Không tìm thấy từ khóa hợp lệ.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return List.of(); // Trả về danh sách rỗng nếu không có từ khóa
    }
    public List<String> searchKnn(String query) throws IOException {
        String answer = chatDetailService.generateAnswer(query);
        List<String> keywords = extractKeywords(answer);
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
    //chat bot
    public String searchLegalDocuments(String userQuery) throws IOException {
        List<String> chunks = searchKnn(userQuery);
        if (chunks.isEmpty()) {
            return "Không tìm thấy văn bản nào phù hợp với câu hỏi của bạn.";
        }
        if (chunks.isEmpty()) {
            return "Không tìm thấy văn bản nào phù hợp với câu hỏi của bạn.";
        }
        String relevantText = chatDetailService.rerankChunks(userQuery, chunks);
        return relevantText;
    }
    public ChatHistory createChatHistory(User user) {
        ChatHistory chatHistory = ChatHistory.builder()
                .user(user)
                .startTime(LocalDateTime.now())
                .status("active")
                .build();
        chatHistory = chatHistoryRepository.save(chatHistory);

        // Sau khi lưu, lấy ID đã được tạo tự động và cập nhật tiêu đề
        chatHistory.setTitle("Đoạn chat " + chatHistory.getId()); // Cập nhật tiêu đề với ID của cuộc trò chuyện

        // Lưu lại đối tượng ChatHistory với tiêu đề mới
        return chatHistoryRepository.save(chatHistory);
    }


    public List<ChatDetailHistory> getMessagesByChatId(Long chatId) {
        ChatHistory chatHistory = new ChatHistory();
        chatHistory.setId(chatId);
        return chatDetailRepository.findByChatHistory(chatHistory);
    }

    public List<ChatHistory> getChatHistoriesByUser(Long userId) {
        return chatHistoryRepository.findByUserIdOrderByStartTimeDesc(userId);
    }
    public ChatHistory getChatHistoryById(Long chatId) {
        return chatHistoryRepository.findById(chatId).orElse(null); // Trả về null nếu không tìm thấy
    }
    public boolean renameChat(Long chatId, String newTitle) {
        Optional<ChatHistory> chatOpt = chatHistoryRepository.findById(chatId);
        if (chatOpt.isPresent()) {
            ChatHistory chat = chatOpt.get();
            chat.setTitle(newTitle);
            chatHistoryRepository.save(chat);
            return true;
        }
        return false;
    }

    public String createShareableLink(Long chatId) {
        Optional<ChatHistory> chatOpt = chatHistoryRepository.findById(chatId);
        if (chatOpt.isPresent()) {
            String baseUrl = "http://localhost:8080/chat/";
            return baseUrl + chatId;
        }
        return null;
    }

    public boolean deleteChat(Long chatId) {
        Optional<ChatHistory> chatHistory = chatHistoryRepository.findById(chatId);
        if (chatHistory.isPresent()) {
            chatHistoryRepository.delete(chatHistory.get());
            return true;
        }
        return false;
    }
}
