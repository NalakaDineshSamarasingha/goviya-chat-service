package com.nalaka.goviya.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nalaka.goviya.model.ChatMessage;
import com.nalaka.goviya.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ChatService chatService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // Map of userId to WebSocketSession
    private final Map<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Extract userId from query params e.g. ws://localhost:8083/ws/chat?userId=123
        String query = session.getUri().getQuery();
        if (query != null && query.contains("userId=")) {
            String userId = UriComponentsBuilder.fromUri(session.getUri()).build().getQueryParams().getFirst("userId");
            if (userId != null) {
                userSessions.put(userId, session);
                log.info("WebSocket connection established for user: {}", userId);
            }
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("Received message: {}", payload);

        try {
            // Parse incoming JSON message
            IncomingMessage incomingMessage = objectMapper.readValue(payload, IncomingMessage.class);

            // Save to database
            ChatMessage savedMessage = chatService.saveMessage(
                    incomingMessage.getSenderId(),
                    incomingMessage.getReceiverId(),
                    incomingMessage.getSenderName(),
                    incomingMessage.getReceiverName(),
                    incomingMessage.getContent()
            );

            // Send to receiver if connected
            WebSocketSession receiverSession = userSessions.get(incomingMessage.getReceiverId());
            if (receiverSession != null && receiverSession.isOpen()) {
                receiverSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(savedMessage)));
            }

            // Also send back to sender as confirmation (optional)
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(savedMessage)));

        } catch (Exception e) {
            log.error("Error processing websocket message", e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        userSessions.values().remove(session);
        log.info("WebSocket connection closed");
    }

    // Inner class for parsing incoming messages
    @lombok.Data
    public static class IncomingMessage {
        private String senderId;
        private String receiverId;
        private String senderName;
        private String receiverName;
        private String content;
    }
}
