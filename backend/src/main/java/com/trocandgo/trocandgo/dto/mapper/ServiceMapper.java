package com.trocandgo.trocandgo.dto.mapper;

import com.trocandgo.trocandgo.dto.response.SearchResultEntryResponse;
import com.trocandgo.trocandgo.entity.Services;
import com.trocandgo.trocandgo.service.AuthService;
import com.trocandgo.trocandgo.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServiceMapper {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    public SearchResultEntryResponse toSearchResponse(Services service) {
        var entry = new SearchResultEntryResponse();

        entry.setId(service.getId().toString());
        entry.setTitle(service.getTitle());
        entry.setDescription(service.getDescription());
        entry.setCategory(service.getCategory().getTitle());
        entry.setStatus(service.getStatus().getTitle().name());
        entry.setType(service.getType().getTitle().name());
        entry.setCity(service.getAdress().getCity());
        entry.setCreationDate(service.getCreationDate());
        entry.setCreatedBy(service.getCreatedBy().getName());
        entry.setCreatorProfilePicture(service.getCreatedBy().getPicture());
        entry.setMail(service.getCreatedBy().getEmail());
        entry.setPhoneNumber(service.getCreatedBy().getPhoneNumber());

        try {
            var loggedInUser = authService.getLoggedInUser();

            var isOwner = service.getCreatedBy() == loggedInUser;
            entry.setOwner(isOwner);

            var isFavorite = userService.isServiceFavorited(service);
            entry.setFavorite(isFavorite);
        } catch(Exception e) {
            entry.setOwner(false);
            entry.setFavorite(false);
        }

        return entry;
    }
}

