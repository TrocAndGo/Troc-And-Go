package com.trocandgo.trocandgo.security;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.trocandgo.trocandgo.service.JwtService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String path = request.getRequestURI();
        // Exclure les routes de signup et login, mais pas logout
        if (path.startsWith("/api/v1/auth/")) {
            if (path.equals("/api/v1/auth/login") || path.equals("/api/v1/auth/signup")) {
                // Si c'est une route login ou signup, on passe au filtre suivant sans traitement
                chain.doFilter(request, response);
                return;
            }
        }

        logger.info("Appel de la fonction doFilterInternal!");

        // Récupérer le header Authorization
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        // Extraire le token
        final String jwtToken = authHeader.substring(7);

        try {
            // Déléguer la validation du token au service
            JwtService.UserInfo userInfo = jwtService.extractUserInfoFromToken(jwtToken);

            // Créer une liste d'autorités basées sur les rôles
            List<GrantedAuthority> authorities = userInfo.getRoles().stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            // Créer un objet UserDetails
            UserDetails userDetails = User.builder()
                    .username(userInfo.getUsername())
                    .password("") // Pas utilisé ici
                    .authorities(authorities)
                    .build();

            // Créer une authentification
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Stocker l'utilisateur dans le contexte de sécurité
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            logger.error("Erreur lors de la validation ou extraction du token: " + e.getMessage(), e);
            e.printStackTrace();
            // En cas d'erreur, ignorer l'authentification et continuer
            SecurityContextHolder.clearContext();
        }

        // Continuer la chaîne de filtres
        chain.doFilter(request, response);
    }
}
