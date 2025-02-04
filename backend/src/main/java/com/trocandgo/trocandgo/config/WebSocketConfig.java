package com.trocandgo.trocandgo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


import com.trocandgo.trocandgo.security.JwtWebSocketInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Ajoute ton intercepteur à l'endpoint WebSocket
        registry.addEndpoint("/ws-chat")
            .setAllowedOriginPatterns("https://localhost:4200")
            .setAllowedOrigins("https://localhost:4200")
            .addInterceptors(new JwtWebSocketInterceptor()) // Ajout de l'intercepteur pour JWT
            .withSockJS();
    }
}
