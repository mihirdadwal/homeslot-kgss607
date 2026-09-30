package com.kgs.homeslot.module.communication.controller;

import com.kgs.homeslot.common.response.ApiResponse;
import com.kgs.homeslot.module.communication.dto.ChatMessageDto;
import com.kgs.homeslot.module.communication.dto.SendChatMessageRequest;
import com.kgs.homeslot.module.communication.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
@PreAuthorize("isAuthenticated()")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<ChatMessageDto>> sendMessage(
            @Valid @RequestBody SendChatMessageRequest request,
            Authentication auth) {
        ChatMessageDto sent = chatService.sendMessage(request, auth.getName());
        return ResponseEntity.ok(ApiResponse.success("Message sent successfully", sent));
    }

    @GetMapping("/conversation")
    public ResponseEntity<ApiResponse<List<ChatMessageDto>>> getConversation(
            @RequestParam Long propertyId,
            @RequestParam Long otherUserId,
            Authentication auth) {
        List<ChatMessageDto> conversation = chatService.getConversation(propertyId, otherUserId, auth.getName());
        return ResponseEntity.ok(ApiResponse.success("Conversation history fetched", conversation));
    }

    @GetMapping("/user-messages")
    public ResponseEntity<ApiResponse<List<ChatMessageDto>>> getUserConversations(Authentication auth) {
        List<ChatMessageDto> messages = chatService.getUserConversations(auth.getName());
        return ResponseEntity.ok(ApiResponse.success("User chat messages fetched", messages));
    }
}
