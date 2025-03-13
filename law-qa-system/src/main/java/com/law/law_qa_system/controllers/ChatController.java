package com.law.law_qa_system.controllers;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.law.law_qa_system.models.Account;
import com.law.law_qa_system.models.ChatDetailHistory;
import com.law.law_qa_system.models.ChatHistory;
import com.law.law_qa_system.models.User;
import com.law.law_qa_system.repositories.UserRepository;
import com.law.law_qa_system.services.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;
    @Autowired
    private UserService userService;
    @Autowired
    private ChatDetailService chatDetailService;
    @GetMapping("")
    public String chat(Principal principal, Model model) {

        String email = principal.getName();
        User user = userService.getUserByEmail(email);
        if (principal == null) {
            return "redirect:/";
        }

        List<ChatHistory> chatHistories = chatService.getChatHistoriesByUser(user.getId());
        model.addAttribute("user", user); // Truyền User vào Model
        System.out.println("Chat Histories: " + chatHistories);
        model.addAttribute("chatHistories", chatHistories);
        return "ChatAI";
    }
    @GetMapping("/")
    public ResponseEntity<String> searchLegalDocuments(@RequestParam String userQuery) {
        try {
            String result = chatService.searchLegalDocuments(userQuery);
            System.out.println(result);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(result);  // Chuyển result thành JsonNode

            // Lấy giá trị content từ node
            String content = rootNode.path("choices").get(0).path("message").path("content").asText();

            return ResponseEntity.ok(content);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Lỗi hệ thống: " + e.getMessage());
        }
    }
    @GetMapping("/messages")
    @ResponseBody
    public List<ChatDetailHistory> getMessages(@RequestParam Long chatId) {
        // Kiểm tra chatId hợp lệ và trả về danh sách tin nhắn
        List<ChatDetailHistory> messages = chatService.getMessagesByChatId(chatId);
        if (messages == null || messages.isEmpty()) {
            return Collections.emptyList(); // Trả về mảng rỗng nếu không có tin nhắn
        }
        return messages; // Trả về danh sách tin nhắn
    }
    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestParam(value = "message") String message,
                                              @RequestParam(value = "chatId", required = false) Long chatId,
                                              HttpSession session, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            if (principal == null) {
                return new ResponseEntity<>("Vui lòng đăng nhập để tiếp tục sử dụng dịch vụ.", HttpStatus.UNAUTHORIZED);
            }
            String email = principal.getName();
            User user = userService.getUserByEmail(email);

            if (user.getTokens() <= 0) {
                redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có đủ token để sử dụng dịch vụ chat. Vui lòng nạp thêm token.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("redirect:/subscription/");
            }
            int requiredTokens = calculateTokens(message); // Tính toán số token cần thiết cho tin nhắn
            if (user.getTokens() < requiredTokens) {
                return new ResponseEntity<>("Bạn không có đủ token để sử dụng dịch vụ chat.", HttpStatus.BAD_REQUEST);
            }


            // Kiểm tra message có hợp lệ không
            if (message == null || message.trim().isEmpty()) {
                return new ResponseEntity<>("Message không thể để trống.", HttpStatus.BAD_REQUEST);
            }

            ChatHistory chatHistory;

            if (chatId == null) {
                // Nếu chatId không có, tạo mới ChatHistory
                chatHistory = chatService.createChatHistory(user);
            } else {
                // Nếu chatId có, tìm ChatHistory và thêm tin nhắn vào đó
                chatHistory = chatService.getChatHistoryById(chatId);
                if (chatHistory == null) {
                    return new ResponseEntity<>("ChatHistory không tồn tại.", HttpStatus.NOT_FOUND);
                }
            }
            String aiResponse = generateAIResponse(message);
            // Tạo ChatDetail và lưu vào cơ sở dữ liệu
            user.setTokens(user.getTokens() - requiredTokens); // Trừ số token tương ứng với tin nhắn
            userService.updateUser(user);
            ChatDetailHistory chatDetail = createChatDetail(chatHistory, message, aiResponse,  user, session);
            chatDetailService.saveChatDetailHistory(chatDetail);
            return ResponseEntity.ok(aiResponse);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    private int calculateTokens(String message) {
        String[] tokens = message.split("\\s+|(?=[,.!?;])|(?<=[,.!?;])");
        return tokens.length;
    }

    // Phương thức tạo ChatDetail mới
    private ChatDetailHistory createChatDetail(ChatHistory chatHistory, String message, String aiResponse, User user, HttpSession session) throws IOException {
        // Câu hỏi của người dùng
        // Phản hồi từ AI sẽ được thêm sau
        return ChatDetailHistory.builder()
                .user(user)
                .chatHistory(chatHistory)
                .sessionId(session.getId())
                .question(message)  // Câu hỏi của người dùng
                .response(aiResponse)  // Phản hồi từ AI sẽ được thêm sau
                .build();
    }

    // Phương thức giả lập phản hồi từ AI
      private String generateAIResponse(String userQuery) throws IOException {
          try {
              String result = chatService.searchLegalDocuments(userQuery);
              System.out.println(result);
              ObjectMapper objectMapper = new ObjectMapper();
              JsonNode rootNode = objectMapper.readTree(result);
              return rootNode.path("choices").get(0).path("message").path("content").asText();
          } catch (IOException e) {
              return "Lỗi";
          }
    }

   @PostMapping("/new")
    public ResponseEntity<ChatHistory> createNewChat(@RequestBody User user) {
        ChatHistory newChat = chatService.createChatHistory(user);
        return ResponseEntity.ok(newChat);
    }


    @PutMapping("/{chatId}/rename")
    public ResponseEntity<?> renameChat(@PathVariable Long chatId, @RequestBody Map<String, String> request) {
        String newTitle = request.get("newTitle");

        if (newTitle == null || newTitle.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Tên không được để trống"));
        }

        boolean updated = chatService.renameChat(chatId, newTitle);
        if (updated) {
            return ResponseEntity.ok(Map.of("message", "Đổi tên thành công", "newTitle", newTitle));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Không tìm thấy đoạn chat"));
        }
    }

    @PostMapping("/{chatId}/share")
    public ResponseEntity<?> shareChat(@PathVariable Long chatId) {
        String shareableLink = chatService.createShareableLink(chatId);
        if (shareableLink == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Không tìm thấy đoạn chat"));
        }
        return ResponseEntity.ok(Map.of("shareableLink", shareableLink));
    }
    @GetMapping("/{chatId}")
    public String getChat(@PathVariable Long chatId, Model model) {
        List<ChatDetailHistory> messages = chatService.getMessagesByChatId(chatId);

        for (ChatDetailHistory message : messages) {

            String content = message.getResponse();  // Phương thức đã được hướng dẫn
            message.setResponse(content);  // Cập nhật lại trường response
        }
        // Thêm danh sách tin nhắn vào model
        model.addAttribute("messages", messages);

        return "chat"; // Trả về template chat.html
    }

    @Transactional
    @DeleteMapping("/delete/{chatId}")
    public ResponseEntity<String> deleteChat(@PathVariable Long chatId) {
        chatDetailService.deleteByChatHistoryId(chatId);
        boolean deleted = chatService.deleteChat(chatId);

        if (deleted) {
            return ResponseEntity.ok("Chat deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Chat detail not found");
        }
    }
}

