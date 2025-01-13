package com.trocandgo.trocandgo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.trocandgo.trocandgo.dto.request.SetAdressRequest;
import com.trocandgo.trocandgo.entity.Adresses;
import com.trocandgo.trocandgo.entity.Favorites;
import com.trocandgo.trocandgo.entity.FavoritesPK;
import com.trocandgo.trocandgo.exception.FavoriteAlreadyExistsException;
import com.trocandgo.trocandgo.exception.FavoriteDoesntExistException;
import com.trocandgo.trocandgo.exception.FavoriteOwnServiceException;
import com.trocandgo.trocandgo.exception.NotAuthenticatedException;
import com.trocandgo.trocandgo.repository.AddressRepository;
import com.trocandgo.trocandgo.repository.FavoriteRepository;
import com.trocandgo.trocandgo.repository.UserRepository;

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

    /**
     * Sets the address for the currently logged-in user.
     *
     * @param request the {@link SetAdressRequest} containing address details.
     * @return the newly created {@link Adresses} object associated with the user.
     * @throws NotAuthenticatedException if there is no authenticated user or the user is not logged in.
     */
    public Adresses setAdress(SetAdressRequest request) {
        var user = authService.getLoggedInUser();

        Adresses adress = new Adresses(request.getAdress(), request.getCity(), request.getZipCode(), request.getDepartment(), request.getRegion(), request.getCountry());
        adress.setLatitude(request.getLatitude());
        adress.setLongitude(request.getLongitude());
        adress = addressRepository.save(adress);

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
}
