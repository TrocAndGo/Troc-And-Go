package com.trocandgo.trocandgo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER')")
        public ResponseEntity<String> getUserProfile() {

        // Récupérer l'authentification depuis le SecurityContextHolder
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is not authenticated");
        }

        // Récupérer le nom d'utilisateur à partir de l'authentification
        String username = authentication.getName();

        return ResponseEntity.ok("User Profile Data for: " + username);
    }
}
