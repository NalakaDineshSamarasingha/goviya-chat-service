package com.nalaka.goviya.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "chat_sessions")
public class ChatSession {
    @Id
    private String id;
    private String user1Id;
    private String user2Id;
    private String user1Name;
    private String user2Name;
    private String lastMessage;
    private Date lastUpdated;
    private int unreadCountUser1;
    private int unreadCountUser2;
}
