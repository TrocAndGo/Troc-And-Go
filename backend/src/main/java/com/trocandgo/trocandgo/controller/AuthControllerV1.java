package com.trocandgo.trocandgo.controller;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trocandgo.trocandgo.dto.request.LoginRequest;
import com.trocandgo.trocandgo.dto.request.SignupRequest;
import com.trocandgo.trocandgo.entity.Roles;
import com.trocandgo.trocandgo.entity.Users;
import com.trocandgo.trocandgo.entity.enums.RoleName;
import com.trocandgo.trocandgo.repository.UserRepository;
import com.trocandgo.trocandgo.service.JwtService;
import com.trocandgo.trocandgo.service.UserDetailsImpl;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthControllerV1 {
    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationProvider authenticationProvider;

    @Autowired
    JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            List<String> roles = userDetails.getAuthorities()
                                    .stream()
                                    .map(grantedAuthority -> grantedAuthority.getAuthority())
                                    .toList();

            String jwtToken = jwtService.generateToken(userDetails.getUsername(), roles);

            return ResponseEntity.ok(Map.of(
                "message", "Login successful",
                "token", jwtToken
            ));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Invalid username or password"
            ));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest signupRequest) {
        if (signupRequest.getUsername() == null || signupRequest.getEmail() == null) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Username and Email are required"
            ));
        }

        if (userRepository.existsByName(signupRequest.getUsername())) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Username is already taken"
            ));
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Email is already in use"
            ));
        }

        Set<String> strRoles = signupRequest.getRoles();
        if (strRoles == null || strRoles.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Missing user role"
            ));
        }

        Set<Roles> roles = new HashSet<>();
        for (String strRole : strRoles) {
            try {
                RoleName name = RoleName.valueOf(strRole);
                roles.add(new Roles(name));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Unknown role: " + strRole
                ));
            }
        }

        Users user = new Users(
            signupRequest.getUsername(),
            signupRequest.getEmail(),
            passwordEncoder.encode(signupRequest.getPassword())
        );
        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of(
            "message", "User registered successfully"
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7); // Extraire le token

            try {
                // Révoquer le token JWT en l'ajoutant à Redis (ou un autre store)
                jwtService.revokeToken(token);

                // Effacer l'authentification dans le contexte de sécurité
                SecurityContextHolder.clearContext();
                System.out.println("Authentication cleared");

                return ResponseEntity.ok("Successfully logged out");
            } catch (Exception e) {
                // En cas d'erreur, retour d'une réponse interne avec message d'erreur
                System.err.println("Erreur lors de la révocation du token : " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                    .body("Token revocation failed: " + e.getMessage());
            }
        }

        // Si le token est absent ou mal formaté, retour d'une mauvaise requête
        return ResponseEntity.badRequest().body("Invalid token");
    }

}
