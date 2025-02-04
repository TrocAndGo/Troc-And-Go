package com.trocandgo.trocandgo.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;

import java.util.Map;

public class JwtWebSocketInterceptor implements HandshakeInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(JwtWebSocketInterceptor.class);

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler handler, Map<String, Object> attributes) throws Exception {
        // Extraire le token JWT des paramètres de l'URL
        String query = request.getURI().getQuery();
        logger.info("Tentative de connexion WebSocket - Query : {}", query);

        if (query != null && query.contains("token=")) {
            String token = query.split("token=")[1];
            logger.info("Token JWT reçu : {}", token);

            if (isValidJwt(token)) {
                logger.info("✅ Token JWT valide. Connexion autorisée.");
                return true;
            } else {
                logger.warn("❌ Token JWT invalide !");
            }
        } else {
            logger.warn("🚫 Aucun token JWT trouvé dans la requête !");
        }

        response.setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler handler, Exception exception) {
        logger.info("Poignée de main WebSocket terminée.");
    }

    private boolean isValidJwt(String token) {
        // Logique de validation réelle du token
        logger.info("Validation du token : {}", token);
        return true; // Remplacer par la logique réelle
    }
}
