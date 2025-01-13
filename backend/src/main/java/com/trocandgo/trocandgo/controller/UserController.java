package com.trocandgo.trocandgo.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trocandgo.trocandgo.dto.request.SetAdressRequest;
import com.trocandgo.trocandgo.model.Adresses;
import com.trocandgo.trocandgo.service.AuthService;
import com.trocandgo.trocandgo.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @GetMapping("hello")
    public ResponseEntity<?> hello() {
        return ResponseEntity.ok("Hello");
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, String>> getUserProfile() {
        var user = authService.getLoggedInUser();

        return ResponseEntity.ok(Map.of("message", "User Profile Data for: " + user.getName()));
    }

    @PutMapping("adress")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Adresses> setAdress(@Valid @RequestBody SetAdressRequest request) {
        var adress = userService.setAdress(request);

        return ResponseEntity.ok(adress);
    }

    @PostMapping("favorites/{serviceId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, String>> addFavorite(@PathVariable(name = "serviceId") String serviceId) {
        userService.addServiceToFavorites(serviceId);

        return ResponseEntity.ok(Map.of("result", "ok"));
    }

    @DeleteMapping("favorites/{serviceId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, String>> deleteFavorite(@PathVariable(name = "serviceId") String serviceId) {
        userService.removeServiceFromFavorites(serviceId);

        return ResponseEntity.ok(Map.of("result", "ok"));
    }
}
