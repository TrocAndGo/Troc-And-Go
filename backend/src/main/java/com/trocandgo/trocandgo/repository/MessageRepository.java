package com.trocandgo.trocandgo.repository;

import com.trocandgo.trocandgo.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderAndReceiverOrReceiverAndSenderOrderByTimestampAsc(String sender, String receiver, String receiver2, String sender2);
}
