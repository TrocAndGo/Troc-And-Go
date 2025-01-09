package com.trocandgo.trocandgo.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trocandgo.trocandgo.dto.request.LoginRequest;
import com.trocandgo.trocandgo.dto.request.SignupRequest;
import com.trocandgo.trocandgo.model.Roles;
import com.trocandgo.trocandgo.model.Users;
import com.trocandgo.trocandgo.model.enums.RoleName;
import com.trocandgo.trocandgo.repository.UserRepository;
import com.trocandgo.trocandgo.services.UserDetailsImpl;

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

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationProvider
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        Map<String, String> response = new HashMap<>();
        response.put("message", "User logged in successfully" + userDetails.getUser().toString());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest signupRequest) {
        if (userRepository.existsByName(signupRequest.getUsername())) {
            return ResponseEntity.badRequest().body("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }

        Set<String> strRoles = signupRequest.getRoles();
        if (strRoles == null || strRoles.isEmpty()) {
            return ResponseEntity.badRequest().body("Missing user role");
        }

        Set<Roles> roles = new HashSet<>();

        strRoles.forEach(strRole -> {
            try {
                RoleName name = RoleName.valueOf(strRole);
                roles.add(new Roles(name));
            } catch (Exception e) {
                throw new RuntimeException("Unknown role: " + strRole);
            }
        });

        Users user = new Users(signupRequest.getUsername(), signupRequest.getEmail(),
                passwordEncoder.encode(signupRequest.getPassword()));
        user.setRoles(roles);
        userRepository.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "User registered successfully");

        return ResponseEntity.ok(response);

    }
}
