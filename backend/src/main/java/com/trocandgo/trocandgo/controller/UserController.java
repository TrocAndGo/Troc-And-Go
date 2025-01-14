package com.trocandgo.trocandgo.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.trocandgo.trocandgo.dto.request.AddFavoriteRequest;
import com.trocandgo.trocandgo.dto.request.CreateReviewRequest;
import com.trocandgo.trocandgo.dto.request.CreateServiceRequest;
import com.trocandgo.trocandgo.dto.request.SetAdressRequest;
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

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ServiceTypeRepository serviceTypeRepository;

    @Autowired
    private ServiceCategoryRepository serviceCategoryRepository;

    @Autowired
    private ServiceStatusRepository serviceStatusRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @GetMapping("hello")
    public ResponseEntity<?> hello() {
        return ResponseEntity.ok("Hello");
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER')")
        public ResponseEntity<Map<String, String>> getUserProfile() {

        // Récupérer l'authentification depuis le SecurityContextHolder
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "User is not authenticated"));
        }

        // Récupérer le nom d'utilisateur à partir de l'authentification
        String username = authentication.getName();

        return ResponseEntity.ok(Map.of("message", "User Profile Data for: " + username));
    }

    @PutMapping("adress")
    public ResponseEntity<?> setAdress(@Valid @RequestBody SetAdressRequest request) {
        Optional<Users> user = userRepository.findById(request.getUserId()); //TODO: Replace by current logged in user
        if (!user.isPresent())
            return ResponseEntity.badRequest().body("User not logged in.");

        Adresses adress = new Adresses(request.getAdress(), request.getCity(), request.getZipCode(), request.getDepartment(), request.getRegion(), request.getCountry());
        adress.setLatitude(request.getLatitude());
        adress.setLongitude(request.getLongitude());
        addressRepository.save(adress);

        user.get().setAddress(adress);
        userRepository.save(user.get());

        return ResponseEntity.ok().body("Adress set !\n" + user.get());
    }

    @PostMapping("service")
    public ResponseEntity<?> createService(@Valid @RequestBody CreateServiceRequest request) {
        Optional<Users> user = userRepository.findById(request.getUserId()); //TODO: Replace by current logged in user
        if (!user.isPresent())
            return ResponseEntity.badRequest().body("User not logged in.");

        Optional<ServiceCategories> category = serviceCategoryRepository.findById(request.getCategoryId());
        if (!category.isPresent())
            return ResponseEntity.badRequest().body("Category does not exists.");

        ServiceTypes type = serviceTypeRepository.findByTitle(request.getType())
                            .orElse(new ServiceTypes(request.getType()));
        ServiceStatuses status = serviceStatusRepository.findByTitle(request.getStatus())
                            .orElse(new ServiceStatuses(request.getStatus()));

        Services service = new Services(request.getTitle(), user.get(), type, status, category.get(), user.get().getAddress());
        service.setDescription(request.getDescription());
        serviceRepository.save(service);

        return ResponseEntity.ok(service.toString());
    }

    @PostMapping("favorites")
    public ResponseEntity<?> addFavorite(@Valid @RequestBody AddFavoriteRequest request) {
        Optional<Users> user = userRepository.findById(request.getUserId()); //TODO: Replace by current logged in user
        if (!user.isPresent())
            return ResponseEntity.badRequest().body("User not logged in.");

        Optional<Services> service = serviceRepository.findById(request.getServiceId());
        if (!service.isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no service found with this id");

        if (service.get().getCreatedBy() == user.get())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Favoriting a service created by self is not allowed");

        FavoritesPK favoritesId = new FavoritesPK(user.get(), service.get());
        if (favoriteRepository.existsById(favoritesId))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "service is already in favorites");

        Favorites favorite = new Favorites(favoritesId.getUser(), favoritesId.getService());
        favoriteRepository.save(favorite);

        return ResponseEntity.status(HttpStatus.CREATED).body("Service succesfully added to favorites");
    }

    @DeleteMapping("favorites/{serviceId}")
    public ResponseEntity<?> deleteFavorite(@PathVariable(name = "serviceId") UUID serviceId, @Valid @RequestBody AddFavoriteRequest request) { //TODO: RequestBody only used for debugging until logging is implemented
        Optional<Users> user = userRepository.findById(request.getUserId()); //TODO: Replace by current logged in user
        if (!user.isPresent())
            return ResponseEntity.badRequest().body("User not logged in.");

        Optional<Services> service = serviceRepository.findById(serviceId);
        if (!service.isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no service found with this id");

        FavoritesPK favoritesId = new FavoritesPK(user.get(), service.get());
        if (!favoriteRepository.existsById(favoritesId))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "service is not in favorites");

        favoriteRepository.deleteById(favoritesId);

        return ResponseEntity.ok("service successfully removed from favorites");
    }


    @PostMapping("services/{id}/reviews")
    public ResponseEntity<?> createReview(@PathVariable(value = "id") UUID serviceId, @Valid @RequestBody CreateReviewRequest request) {
        Optional<Users> user = userRepository.findById(request.getUserId()); //TODO: Replace by current logged in user
        if (!user.isPresent())
            return ResponseEntity.badRequest().body("User not logged in.");

        Optional<Services> service = serviceRepository.findById(serviceId);

        if (!service.isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Service does not exists.");

        if (service.get().getCreatedBy() == user.get())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A user cannot review its own service");

        ReviewsPK reviewId = new ReviewsPK(user.get(), service.get());
        if (reviewRepository.existsById(reviewId))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A comment from this user already exists on this service.");

        Reviews review = new Reviews(reviewId.getUser(), reviewId.getService(), request.getComment(), request.getRating());
        reviewRepository.save(review);

        return ResponseEntity.ok(Map.of("error", "Review successfully submitted"));
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

    System.out.println("Path in database: " + user.getPicture());
System.out.println("Resolved file path: " + filePath);
System.out.println("File exists: " + Files.exists(filePath));

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
