package com.example.studentmanagement.controller;

import com.example.studentmanagement.dto.ChatRequest;
import com.example.studentmanagement.dto.ChatResponse;
import com.example.studentmanagement.service.ChatbotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/chatbot")
@Slf4j
public class ChatbotController {

    @Autowired
    private ChatbotService chatbotService;

    /**
     * Send a message to the chatbot
     * POST /api/chatbot/chat
     */
    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ChatResponse errorResponse = new ChatResponse();
            errorResponse.setSuccess(false);
            errorResponse.setError("Invalid request: " + bindingResult.getFieldError().getDefaultMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        log.info("Received chat message: {}", request.getMessage());
        ChatResponse response = chatbotService.chat(request);
        
        return response.isSuccess() ? 
                ResponseEntity.ok(response) : 
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Send a message to the chatbot with current student database context
     * POST /api/chatbot/chat-with-context
     */
    @PostMapping("/chat-with-context")
    public ResponseEntity<ChatResponse> chatWithContext(@Valid @RequestBody ChatRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ChatResponse errorResponse = new ChatResponse();
            errorResponse.setSuccess(false);
            errorResponse.setError("Invalid request: " + bindingResult.getFieldError().getDefaultMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        log.info("Received context-aware chat message: {}", request.getMessage());
        ChatResponse response = chatbotService.chatWithContext(request);
        
        return response.isSuccess() ? 
                ResponseEntity.ok(response) : 
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Clear conversation history
     * POST /api/chatbot/clear/{conversationId}
     */
    @PostMapping("/clear/{conversationId}")
    public ResponseEntity<String> clearConversation(@PathVariable String conversationId) {
        chatbotService.clearConversation(conversationId);
        log.info("Conversation cleared: {}", conversationId);
        return ResponseEntity.ok("Conversation cleared successfully");
    }

    /**
     * Check if a conversation exists
     * GET /api/chatbot/exists/{conversationId}
     */
    @GetMapping("/exists/{conversationId}")
    public ResponseEntity<Boolean> conversationExists(@PathVariable String conversationId) {
        boolean exists = chatbotService.conversationExists(conversationId);
        return ResponseEntity.ok(exists);
    }

    /**
     * Health check endpoint
     * GET /api/chatbot/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Chatbot service is running");
    }
}