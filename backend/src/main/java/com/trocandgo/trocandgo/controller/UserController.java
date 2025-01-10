package com.trocandgo.trocandgo.controller;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.trocandgo.trocandgo.dto.request.AddFavoriteRequest;
import com.trocandgo.trocandgo.dto.request.CreateServiceRequest;
import com.trocandgo.trocandgo.dto.request.SetAdressRequest;
import com.trocandgo.trocandgo.model.Adresses;
import com.trocandgo.trocandgo.model.Favorites;
import com.trocandgo.trocandgo.model.FavoritesPK;
import com.trocandgo.trocandgo.model.ServiceCategories;
import com.trocandgo.trocandgo.model.ServiceStatuses;
import com.trocandgo.trocandgo.model.ServiceTypes;
import com.trocandgo.trocandgo.model.Services;
import com.trocandgo.trocandgo.model.Users;
import com.trocandgo.trocandgo.repository.AddressRepository;
import com.trocandgo.trocandgo.repository.FavoriteRepository;
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

    @GetMapping("hello")
    public ResponseEntity<?> hello() {
        return ResponseEntity.ok("Hello");
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
}
