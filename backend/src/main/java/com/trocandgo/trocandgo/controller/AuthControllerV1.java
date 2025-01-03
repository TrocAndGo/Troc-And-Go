package com.trocandgo.trocandgo.controller;
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
import com.trocandgo.trocandgo.model.Role;
import com.trocandgo.trocandgo.model.Account;
import com.trocandgo.trocandgo.repository.AccountRepository;
import com.trocandgo.trocandgo.services.JwtService;
import com.trocandgo.trocandgo.services.UserDetailsImpl;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthControllerV1 {
    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationProvider authenticationProvider;

    @Autowired
    JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationProvider
            .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwtToken = jwtService.generateToken(userDetails.getUsername());

        return ResponseEntity.ok().body(Map.of(
            "message", "Login Success",
            "token", jwtToken,
            "user", userDetails.getAccount()
        ));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest signupRequest) {
        if (accountRepository.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity.badRequest().body("Error: Username is already taken!");
        }

        if (accountRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }

        Set<String> strRoles = signupRequest.getRoles();
        if (strRoles == null || strRoles.isEmpty()) {
            return ResponseEntity.badRequest().body("Missing user role");
        }

        Set<Role> roles = new HashSet<>();

        strRoles.forEach(strRole -> {
            try {
                Role role = Role.valueOf(strRole);
                roles.add(role);
            } catch (Exception e) {
                throw new RuntimeException("Unknown role: " + strRole);
            }
        });

        Account user = new Account(signupRequest.getUsername(),signupRequest.getEmail(),
            passwordEncoder.encode(signupRequest.getPassword()), roles);
        accountRepository.save(user);

        return ResponseEntity.ok("User registered succesfully");
    }
}
