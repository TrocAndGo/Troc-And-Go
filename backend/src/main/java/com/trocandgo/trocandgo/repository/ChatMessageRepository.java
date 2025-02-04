package com.trocandgo.trocandgo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trocandgo.trocandgo.entity.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
}
