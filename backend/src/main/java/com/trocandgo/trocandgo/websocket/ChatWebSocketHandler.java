package com.trocandgo.trocandgo.websocket;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class ChatWebSocketHandler extends TextWebSocketHandler {

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Log du message reçu
        System.out.println("Message reçu : " + message.getPayload());
        // Réponse écho pour tester
        session.sendMessage(new TextMessage("Réponse du serveur : " + message.getPayload()));
    }
}
