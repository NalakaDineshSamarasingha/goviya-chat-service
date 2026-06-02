package com.nalaka.goviya.repository;

import com.nalaka.goviya.model.ChatSession;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatSessionRepository extends MongoRepository<ChatSession, String> {
    List<ChatSession> findByUser1IdOrUser2IdOrderByLastUpdatedDesc(String user1Id, String user2Id);
    Optional<ChatSession> findByUser1IdAndUser2Id(String user1Id, String user2Id);
}
