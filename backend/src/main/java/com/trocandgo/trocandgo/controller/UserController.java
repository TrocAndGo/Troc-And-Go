package com.trocandgo.trocandgo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.Optional;
import java.util.UUID;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import com.trocandgo.trocandgo.services.ImageService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

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

    @Autowired
    private ImageService imageService;

    @GetMapping("hello")
    public ResponseEntity<?> hello() {
        return ResponseEntity.ok("Hello");
    }

    @Transactional
    @PostMapping("/profile/upload-picture")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> uploadProfilePicture(@RequestParam("image") MultipartFile image,
                                                Authentication authentication) {
        if (image.isEmpty()) {
            return ResponseEntity.badRequest().body("Image file is required.");
        }

        try {
            // Récupérer l'utilisateur actuel
            String username = authentication.getName();
            Users user = userRepository.findByName(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Définir le répertoire de stockage des images
            Path uploadDirectory = Paths.get("src/main/resources/static/uploads/profile-pictures");

            // Vérifier si l'utilisateur a déjà une image et la supprimer
            if (user.getPicture() != null && !user.getPicture().isEmpty()) {
                String oldFilePath = user.getPicture().replace("/uploads/profile-pictures/", "");
                Path oldFile = uploadDirectory.resolve(oldFilePath);
                if (Files.exists(oldFile)) Files.delete(oldFile);
            }

            // Utiliser le service pour traiter et crypter l'image
            String imagePath = imageService.processAndEncryptImage(image, username, uploadDirectory);

            // Mettre à jour le chemin de l'image dans la base de données
            user.setPicture(imagePath);
            userRepository.save(user);

            return ResponseEntity.ok("Profile picture uploaded successfully.");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Failed to upload profile picture: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload profile picture.");
        }
    }

    @GetMapping("/profile/download-picture")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> downloadProfilePicture(Authentication authentication) {
        try {
            // Récupérer l'utilisateur actuel
            String username = authentication.getName();
            Users user = userRepository.findByName(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Vérifier si l'utilisateur a une image
            if (user.getPicture() == null || user.getPicture().isEmpty()) {
                return ResponseEntity.badRequest().body("No profile picture found.");
            }

            // Définir le répertoire de stockage des images
            Path uploadDirectory = Paths.get("src/main/resources/static/uploads/profile-pictures");

            // Obtenir le chemin complet du fichier
            String fileName = user.getPicture().replace("/uploads/profile-pictures/", "");
            Path filePath = uploadDirectory.resolve(fileName);

            // Vérifier si le fichier existe
            if (!Files.exists(filePath)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profile picture not found.");
            }

            // Utiliser le service pour décrypter l'image
            byte[] decryptedImage = imageService.decryptImage(filePath);

            // Retourner l'image décryptée avec le bon type MIME
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(decryptedImage);

        } catch (Exception e) {
            logger.error("Error downloading profile picture: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to download profile picture.");
        }
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

        return ResponseEntity.ok("Review successfully submitted");
    }
}
