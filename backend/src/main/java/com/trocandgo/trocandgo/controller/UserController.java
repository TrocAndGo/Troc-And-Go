package com.trocandgo.trocandgo.controller;


import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trocandgo.trocandgo.dto.request.AddFavoriteRequest;
import com.trocandgo.trocandgo.dto.request.CreateReviewRequest;
import com.trocandgo.trocandgo.dto.request.CreateServiceRequest;
import com.trocandgo.trocandgo.dto.request.SetAdressRequest;
import com.trocandgo.trocandgo.dto.request.UploadProfilePictureRequest;
import com.trocandgo.trocandgo.dto.response.UploadProfilePictureResponse;
import com.trocandgo.trocandgo.model.Adresses;
import com.trocandgo.trocandgo.model.Favorites;
import com.trocandgo.trocandgo.model.FavoritesPK;
import com.trocandgo.trocandgo.model.Reviews;
import com.trocandgo.trocandgo.model.ReviewsPK;
import com.trocandgo.trocandgo.model.ServiceCategories;
import com.trocandgo.trocandgo.model.ServiceStatuses;
import com.trocandgo.trocandgo.model.ServiceTypes;
import com.trocandgo.trocandgo.model.Services;
import com.trocandgo.trocandgo.model.Users;
import com.trocandgo.trocandgo.repository.AddressRepository;
import com.trocandgo.trocandgo.repository.FavoriteRepository;
import com.trocandgo.trocandgo.repository.ReviewRepository;
import com.trocandgo.trocandgo.repository.ServiceCategoryRepository;
import com.trocandgo.trocandgo.repository.ServiceRepository;
import com.trocandgo.trocandgo.repository.ServiceStatusRepository;
import com.trocandgo.trocandgo.repository.ServiceTypeRepository;
import com.trocandgo.trocandgo.repository.UserRepository;
import com.trocandgo.trocandgo.service.ImageService;


import com.trocandgo.trocandgo.dto.request.SetAdressRequest;
import com.trocandgo.trocandgo.entity.Adresses;
import com.trocandgo.trocandgo.service.AuthService;
import com.trocandgo.trocandgo.service.UserService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @Autowired
    private ImageService imageService;

    @GetMapping("hello")
    public ResponseEntity<?> hello() {
        return ResponseEntity.ok("Hello");
    }

    @Transactional
    @PostMapping("/profile/upload-picture")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UploadProfilePictureResponse> uploadProfilePicture(
        @Valid @ModelAttribute UploadProfilePictureRequest request,
        Authentication authentication) {

        try {
            // Appeler le service ImageService pour gérer l'image
            imageService.uploadProfilePicture(request.getImage(), authentication.getName());

            return ResponseEntity.ok(new UploadProfilePictureResponse(
                "success",
                "Profile picture uploaded successfully."));

        } catch (Exception e) {
            logger.error("Failed to upload profile picture: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new UploadProfilePictureResponse(
                    "error", "Failed to upload profile picture.")
            );
        }
    }

    @GetMapping("/profile/download-picture")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> downloadProfilePicture(Authentication authentication) {
        try {
            // Appeler le service pour gérer le téléchargement et la décryption
            byte[] decryptedImage = imageService.downloadProfilePicture(authentication.getName());

            // Retourner l'image décryptée avec le bon type MIME
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(decryptedImage);

        } catch (Exception e) {
            logger.error("Failed to download profile picture: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "status", "error",
                "message", "An error occurred while processing your request. Please try again later."
            ));
        }
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

@PostMapping("/upload")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> uploadProfilePicture(
            @RequestParam("image") MultipartFile image,
            Authentication authentication) {
        if (image.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Image file is required."));
        }
        try {
            // Récupérer l'utilisateur actuel
            String username = authentication.getName();
            Users user = userRepository.findByName(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            // Définir le répertoire de stockage des images
            Path uploadDirectory = Paths.get("src/main/resources/static/uploads");
            // Vérifier si l'utilisateur a déjà une image et la supprimer
            if (user.getPicture() != null && !user.getPicture().isEmpty()) {
                // Extraire le nom de fichier à partir de l'URL actuelle
                String oldFilePath = user.getPicture().replace("/uploads/", "");
                Path oldFile = uploadDirectory.resolve(oldFilePath);
                // Supprimer l'ancien fichier si il existe
                if (Files.exists(oldFile)) {
                    try {
                        Files.delete(oldFile);
                    } catch (IOException e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Map.of("error","Failed to delete the old profile picture: "+ e.getMessage()));
                    }
                }
            }
            // Générer un nom de fichier unique pour la nouvelle image
            String fileName = username + "_" + System.currentTimeMillis() + ".jpg";
            Path filePath = uploadDirectory.resolve(fileName);
            // Sauvegarder la nouvelle image
            image.transferTo(filePath);
            // Mettre à jour le chemin de l'image dans la base de données
            user.setPicture("/uploads/" + fileName); userRepository.save(user);
            return ResponseEntity.ok(Map.of("error", "Profile picture uploaded successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to upload profile picture: "+ e.getMessage()));
        }
    }

@GetMapping("/profile-picture")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Resource> getProfilePicture(Authentication authentication) throws IOException {
    String username = authentication.getName();
    Users user = userRepository.findByName(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

    // Récupérer le nom de fichier
    String fileName = user.getPicture() != null ? user.getPicture() : "avatar1.jpg";
Path filePath = Paths.get("src/main/resources/static").resolve(fileName.substring(1));

    // Vérifier si le fichier existe
    if (!Files.exists(filePath)) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    try {
        Resource resource = new UrlResource(filePath.toUri());
        MediaType mediaType = Files.probeContentType(filePath) != null
            ? MediaType.parseMediaType(Files.probeContentType(filePath))
            : MediaType.APPLICATION_OCTET_STREAM;

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(resource);
    } catch (MalformedURLException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
}


}
