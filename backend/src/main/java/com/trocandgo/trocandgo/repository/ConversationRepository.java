package com.trocandgo.trocandgo.repository;

import com.trocandgo.trocandgo.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    @Query("SELECT c FROM Conversation c WHERE (c.user1 = :user1 AND c.user2 = :user2) OR (c.user1 = :user2 AND c.user2 = :user1)")
    Optional<Conversation> findConversationBetweenUsers(@Param("user1") String user1, @Param("user2") String user2);

    @Query("SELECT CASE WHEN c.user1 = :username THEN c.user2 ELSE c.user1 END " +
       "FROM Conversation c WHERE c.user1 = :username OR c.user2 = :username")
    List<String> findUserConversations(@Param("username") String username);


    /*
    @Query("SELECT c FROM Conversation c WHERE c.user1 = :username OR c.user2 = :username")
    List<Conversation> findByUser1OrUser2(@Param("username") String username);
*/
}
