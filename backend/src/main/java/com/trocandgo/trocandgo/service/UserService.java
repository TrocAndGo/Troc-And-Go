package com.trocandgo.trocandgo.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.trocandgo.trocandgo.dto.request.SetAdressRequest;
import com.trocandgo.trocandgo.dto.request.UpdateProfileRequest;
import com.trocandgo.trocandgo.dto.response.ProfileResponse;
import com.trocandgo.trocandgo.entity.Adresses;
import com.trocandgo.trocandgo.entity.Favorites;
import com.trocandgo.trocandgo.entity.FavoritesPK;
import com.trocandgo.trocandgo.entity.Users;
import com.trocandgo.trocandgo.exception.FavoriteAlreadyExistsException;
import com.trocandgo.trocandgo.exception.FavoriteDoesntExistException;
import com.trocandgo.trocandgo.exception.FavoriteOwnServiceException;
import com.trocandgo.trocandgo.exception.NotAuthenticatedException;
import com.trocandgo.trocandgo.repository.AddressRepository;
import com.trocandgo.trocandgo.repository.FavoriteRepository;
import com.trocandgo.trocandgo.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {
    @Autowired
    private AuthService authService;

    @Autowired
    private ServiceService serviceService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;



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

    /**
     * Sets the address for the currently logged-in user.
     *
     * @param request the {@link SetAdressRequest} containing address details.
     * @return the newly created {@link Adresses} object associated with the user.
     * @throws NotAuthenticatedException if there is no authenticated user or the user is not logged in.
     */
    public Adresses setAdress(SetAdressRequest request) {
        // Récupérer l'utilisateur connecté
        var user = authService.getLoggedInUser();

        // Vérifier si l'utilisateur a déjà une adresse
        Adresses adress = user.getAddress();

        if (adress == null) {
            // Si aucune adresse n'existe, en créer une nouvelle
            adress = new Adresses();
        }

        // Mettre à jour les champs de l'adresse
        adress.setAdress(request.getAdress());
        adress.setCity(request.getCity());
        adress.setZipCode(request.getZipCode());
        adress.setDepartment(request.getDepartment());
        adress.setRegion(request.getRegion());
        adress.setCountry(request.getCountry());
        adress.setLatitude(request.getLatitude());
        adress.setLongitude(request.getLongitude());

        // Sauvegarder ou mettre à jour l'adresse
        adress = addressRepository.save(adress);

        // Associer l'adresse à l'utilisateur
        user.setAddress(adress);
        userRepository.save(user);

        return adress;
    }

    /**
     * Adds a specified service to the favorites list of the currently logged-in user.
     *
     * @param serviceId the unique identifier of the service to add to favorites.
     * @throws NotAuthenticatedException if there is no authenticated user or the user is not logged in.
     * @throws ServiceNotFoundException if no service is found with the provided ID.
     * @throws FavoriteOwnServiceException if the logged-in user attempts to favorite their own service.
     * @throws FavoriteAlreadyExistsException if the service is already in the user's favorites list.
     */
    public void addServiceToFavorites(String serviceId) {
        var user = authService.getLoggedInUser();
        var service = serviceService.getServiceById(serviceId);

        if (service.getCreatedBy() == user)
            throw new FavoriteOwnServiceException();

        FavoritesPK favoritesId = new FavoritesPK(user, service);
        if (favoriteRepository.existsById(favoritesId))
            throw new FavoriteAlreadyExistsException();

        Favorites favorite = new Favorites(favoritesId.getUser(), favoritesId.getService());
        favoriteRepository.save(favorite);
    }

    /**
     * Removes a specified service from the favorites list of the currently logged-in user.
     *
     * @param serviceId the unique identifier of the service to remove from favorites.
     * @throws NotAuthenticatedException if there is no authenticated user or the user is not logged in.
     * @throws ServiceNotFoundException if no service is found with the provided ID.
     * @throws FavoriteDoesntExistException if the service is not in the user's favorites list.
     */
    public void removeServiceFromFavorites(String serviceId) {
        var user = authService.getLoggedInUser();
        var service = serviceService.getServiceById(serviceId);

        FavoritesPK favoritesId = new FavoritesPK(user, service);
        if (!favoriteRepository.existsById(favoritesId))
            throw new FavoriteDoesntExistException();

        favoriteRepository.deleteById(favoritesId);
    }

    // Récupérer les informations du profil utilisateur
    public ProfileResponse getUserProfile(Users user) {
        Adresses address = user.getAddress();

        return new ProfileResponse(
            user.getName(),
            user.getEmail(),
            user.getPhoneNumber(),
            address != null ? address.getAdress() : null,
            address != null ? address.getCity() : null,
            address != null ? address.getZipCode() : null
        );
    }

    // Mettre à jour le profil utilisateur
    @Transactional
    public void updateUserProfile(UpdateProfileRequest request, Users user) {
        // Mise à jour des informations personnelles
        if (request.getUsername() != null) {
            user.setName(request.getUsername());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }

        // Mise à jour de l'adresse
        Adresses address = user.getAddress();
        if (address == null) {
            address = new Adresses();
        }

        if (request.getAddress() != null) {
            address.setAdress(request.getAddress());
        }
        if (request.getCity() != null) {
            address.setCity(request.getCity());
        }
        if (request.getZipCode() != null) {
            address.setZipCode(request.getZipCode());
        }
        if (request.getCountry() != null) {
            address.setCountry(request.getCountry());
        }
        if (request.getDepartment() != null) {
            address.setDepartment(request.getDepartment());
        }
        if (request.getRegion() != null) {
            address.setRegion(request.getRegion());
        }
        if (request.getLatitude() != null) {
            address.setLatitude(BigDecimal.valueOf(request.getLatitude()));
        }
        if (request.getLongitude() != null) {
            address.setLongitude(BigDecimal.valueOf(request.getLongitude()));
        }

        address = addressRepository.save(address);
        user.setAddress(address);

        userRepository.save(user);
    }
}
