package com.nalaka.goviya.controller;

import com.nalaka.goviya.model.ChatMessage;
import com.nalaka.goviya.model.ChatSession;
import com.nalaka.goviya.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goviya-chat-core")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/sessions/{userId}")
    public ResponseEntity<List<ChatSession>> getUserSessions(@PathVariable String userId) {
        return ResponseEntity.ok(chatService.getUserSessions(userId));
    }

    @GetMapping("/messages/{sessionId}")
    public ResponseEntity<List<ChatMessage>> getSessionMessages(
            @PathVariable String sessionId,
            @RequestParam(required = false) String userId) {
        
        if (userId != null) {
            chatService.markMessagesAsRead(sessionId, userId);
        }
        return ResponseEntity.ok(chatService.getSessionMessages(sessionId));
    }

    @PostMapping("/sessions")
    public ResponseEntity<ChatSession> getOrCreateSession(@RequestBody java.util.Map<String, String> payload) {
        String senderId = payload.get("senderId");
        String receiverId = payload.get("receiverId");
        String senderName = payload.get("senderName");
        String receiverName = payload.get("receiverName");
        return ResponseEntity.ok(chatService.getOrCreateSession(senderId, receiverId, senderName, receiverName));
    }
}
