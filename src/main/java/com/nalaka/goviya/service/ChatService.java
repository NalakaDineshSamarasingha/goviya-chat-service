package com.nalaka.goviya.service;

import com.nalaka.goviya.model.ChatMessage;
import com.nalaka.goviya.model.ChatSession;
import com.nalaka.goviya.repository.ChatMessageRepository;
import com.nalaka.goviya.repository.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;

    public List<ChatSession> getUserSessions(String userId) {
        return sessionRepository.findByUser1IdOrUser2IdOrderByLastUpdatedDesc(userId, userId);
    }

    public List<ChatMessage> getSessionMessages(String sessionId) {
        return messageRepository.findBySessionIdOrderByTimestampAsc(sessionId);
    }

    public ChatSession getOrCreateSession(String senderId, String receiverId, String senderName, String receiverName) {
        // Since order can vary, we check both combinations
        Optional<ChatSession> sessionOpt = sessionRepository.findByUser1IdAndUser2Id(senderId, receiverId);
        if (sessionOpt.isPresent()) {
            return sessionOpt.get();
        }

        sessionOpt = sessionRepository.findByUser1IdAndUser2Id(receiverId, senderId);
        if (sessionOpt.isPresent()) {
            return sessionOpt.get();
        }

        // Create new session
        ChatSession newSession = ChatSession.builder()
                .user1Id(senderId)
                .user2Id(receiverId)
                .user1Name(senderName)
                .user2Name(receiverName)
                .lastUpdated(new Date())
                .unreadCountUser1(0)
                .unreadCountUser2(0)
                .build();
        return sessionRepository.save(newSession);
    }

    public ChatMessage saveMessage(String senderId, String receiverId, String senderName, String receiverName, String content) {
        ChatSession session = getOrCreateSession(senderId, receiverId, senderName, receiverName);

        ChatMessage message = ChatMessage.builder()
                .sessionId(session.getId())
                .senderId(senderId)
                .receiverId(receiverId)
                .content(content)
                .timestamp(new Date())
                .isRead(false)
                .build();

        ChatMessage savedMessage = messageRepository.save(message);

        // Update session
        session.setLastMessage(content);
        session.setLastUpdated(new Date());
        
        if (session.getUser1Id().equals(receiverId)) {
            session.setUnreadCountUser1(session.getUnreadCountUser1() + 1);
        } else {
            session.setUnreadCountUser2(session.getUnreadCountUser2() + 1);
        }
        
        sessionRepository.save(session);

        return savedMessage;
    }
    
    public void markMessagesAsRead(String sessionId, String userId) {
        Optional<ChatSession> sessionOpt = sessionRepository.findById(sessionId);
        if (sessionOpt.isPresent()) {
            ChatSession session = sessionOpt.get();
            if (session.getUser1Id().equals(userId)) {
                session.setUnreadCountUser1(0);
            } else if (session.getUser2Id().equals(userId)) {
                session.setUnreadCountUser2(0);
            }
            sessionRepository.save(session);
        }
    }
}
