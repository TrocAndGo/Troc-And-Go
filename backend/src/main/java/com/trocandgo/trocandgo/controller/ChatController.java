package com.trocandgo.trocandgo.controller;

import com.trocandgo.trocandgo.entity.Message;
import com.trocandgo.trocandgo.entity.Users;
import com.trocandgo.trocandgo.entity.Conversation;
import com.trocandgo.trocandgo.repository.ConversationRepository;
import com.trocandgo.trocandgo.repository.MessageRepository;
import com.trocandgo.trocandgo.service.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")  // Mise à jour du chemin d'API
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate; // Utilisé pour envoyer des messages via WebSocket

    @Autowired
    private MessageRepository messageRepository; // Repository pour les messages

    @Autowired
    private ConversationRepository conversationRepository; // Repository pour les conversations

    @Autowired
    private AuthService authService;

    // Endpoint pour récupérer l'historique des messages entre deux utilisateurs
    @GetMapping("/conversation")
    //@PreAuthorize("hasRole('USER')")
    public List<Message> getConversation(@RequestParam String user1, @RequestParam String user2) {
        // Recherche de la conversation entre les deux utilisateurs
        Optional<Conversation> conversation = conversationRepository.findConversationBetweenUsers(user1, user2);

        if (conversation.isPresent()) {
            // Si une conversation existe, récupérer les messages associés
            return messageRepository.findBySenderAndReceiverOrReceiverAndSenderOrderByTimestampAsc(user1, user2, user1, user2);
        }

        // Si aucune conversation n'est trouvée, retourner une liste vide ou un message spécifique
        return List.of();
    }

    @GetMapping("/conversations")
    //@PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<String>> getUserConversations() {
        Users currentUser = authService.getLoggedInUser(); // Récupération de l'utilisateur connecté

        List<String> conversationUsers = conversationRepository.findUserConversations(currentUser.getName());

        return ResponseEntity.ok(conversationUsers);
    }

    // Méthode qui gère l'envoi de message via WebSocket
    @MessageMapping("/sendMessage")
    //@PreAuthorize("hasRole('USER')")
    public void sendMessage(Message message) {
        // Sauvegarder le message dans la base de données
        message.setTimestamp(LocalDateTime.now());
        messageRepository.save(message);

        // Mettre à jour ou créer la conversation entre les deux utilisateurs
        updateConversation(message.getSender(), message.getReceiver());

        // Envoyer le message aux utilisateurs (expéditeur et destinataire)
        messagingTemplate.convertAndSendToUser(message.getReceiver(), "/queue/messages", message);
        messagingTemplate.convertAndSendToUser(message.getSender(), "/queue/messages", message);
    }

    // Créer ou mettre à jour la conversation entre deux utilisateurs
    private void updateConversation(String sender, String receiver) {
        // Vérifier si la conversation existe déjà
        Conversation conversation = conversationRepository.findConversationBetweenUsers(sender, receiver).orElse(null);

        if (conversation == null) {
            // Créer une nouvelle conversation
            conversation = new Conversation(sender, receiver, LocalDateTime.now());
            conversationRepository.save(conversation);
        } else {
            // Mettre à jour la conversation (par exemple, modifier la date du dernier message)
            conversation.setLastMessageTimestamp(LocalDateTime.now());
            conversationRepository.save(conversation);
        }
    }
}
