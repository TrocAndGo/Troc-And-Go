package com.trocandgo.trocandgo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.trocandgo.trocandgo.repository.UserRepository;
import com.trocandgo.trocandgo.model.Users;


@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Méthode pour récupérer un utilisateur par son nom
    public Users getUserByUsername(String username) {
        return userRepository.findByName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Méthode pour mettre à jour l'image de profil
    public void updateProfilePicture(Users user, String imagePath) {
        user.setPicture(imagePath);
        userRepository.save(user);
    }
}
