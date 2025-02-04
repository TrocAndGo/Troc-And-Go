package com.trocandgo.trocandgo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String user1;
    private String user2;
    private LocalDateTime lastMessageTimestamp;

    public Conversation() {}

    public Conversation(String user1, String user2, LocalDateTime lastMessageTimestamp) {
        this.user1 = user1;
        this.user2 = user2;
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUser1() { return user1; }
    public void setUser1(String user1) { this.user1 = user1; }
    public String getUser2() { return user2; }
    public void setUser2(String user2) { this.user2 = user2; }
    public LocalDateTime getLastMessageTimestamp() { return lastMessageTimestamp; }
    public void setLastMessageTimestamp(LocalDateTime lastMessageTimestamp) { this.lastMessageTimestamp = lastMessageTimestamp; }
}
