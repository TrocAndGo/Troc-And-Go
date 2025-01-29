package com.trocandgo.trocandgo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trocandgo.trocandgo.dto.mapper.ServiceMapper;
import com.trocandgo.trocandgo.dto.request.SetAdressRequest;
import com.trocandgo.trocandgo.dto.request.UploadProfilePictureRequest;
import com.trocandgo.trocandgo.dto.response.ProfileResponse;
import com.trocandgo.trocandgo.dto.response.SearchResultEntryResponse;
import com.trocandgo.trocandgo.dto.response.UploadProfilePictureResponse;
import com.trocandgo.trocandgo.entity.Adresses;
import com.trocandgo.trocandgo.entity.Users;
import com.trocandgo.trocandgo.service.AuthService;
import com.trocandgo.trocandgo.service.ImageService;
import com.trocandgo.trocandgo.service.ServiceService;
import com.trocandgo.trocandgo.service.UserService;
import com.trocandgo.trocandgo.dto.request.UpdateProfileRequest;
import com.trocandgo.trocandgo.dto.request.UpdateUserPasswordRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private AuthService authService;

    @Autowired
    private ServiceService serviceService;

    @Autowired
    private ServiceMapper serviceMapper;

    @GetMapping("hello")
    public ResponseEntity<?> hello() {
        return ResponseEntity.ok("Hello");
    }

    // Envoyer les informations du profil
    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getUserProfile() {
        try {
            Users currentUser = authService.getLoggedInUser();
            ProfileResponse profileResponse = userService.getUserProfile(currentUser);
            return ResponseEntity.ok(profileResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Failed to get profile."));
        }
    }

    // Mettre à jour le profil de l'utilisateur et l'adresse
    @PutMapping("/profile/update")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateUserProfile(@Valid @RequestBody UpdateProfileRequest request) {
        try {
            Users currentUser = authService.getLoggedInUser();
            userService.updateUserProfile(request, currentUser);
            return ResponseEntity.ok(Map.of("result", "Profile updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "Failed to update profile."));

        }

    }

    // Mettre à jour le mot de passe de l'utilisateur
    @PutMapping("/profile/update-password")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateUserPassword(@Valid @RequestBody UpdateUserPasswordRequest request) {
        try {
            Users currentUser = authService.getLoggedInUser();
            userService.updateUserPassword(request, currentUser);
            return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "status", "error",
                    "message", e.getMessage())); // Affiche un message d'erreur clair
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "An unexpected error occurred while updating the password."));
        }
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
                            "error", "Failed to upload profile picture."));
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
                    "message", "Failed to download profile picture."));
        }
    }

    @PutMapping("/profile/address")
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

    @GetMapping("/me/services")
    @PreAuthorize("hasRole('USER')")
    public Page<SearchResultEntryResponse> getMyServices(
            @SortDefault(sort = "creationDate", direction = Direction.DESC) @PageableDefault(size = 20) Pageable pageable) {
        var services = serviceService.getServicesCreatedByUserPaginated(authService.getLoggedInUser(), pageable);

        return services.map(serviceMapper::toSearchResponse);
    }

    @GetMapping("/me/favorites")
    @PreAuthorize("hasRole('USER')")
    public Page<SearchResultEntryResponse> getMyFavoritesPaginated(@PageableDefault(size = 20) Pageable pageable) {
        var favorites = userService.getMyFavoritesPaginated(pageable);

        return favorites.map(favorite -> serviceMapper.toSearchResponse(favorite.getService()));
    }
}
