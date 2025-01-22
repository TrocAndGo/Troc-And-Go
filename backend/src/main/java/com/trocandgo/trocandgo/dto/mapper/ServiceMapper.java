package com.trocandgo.trocandgo.dto.mapper;

import com.trocandgo.trocandgo.dto.response.SearchResultEntryResponse;
import com.trocandgo.trocandgo.entity.Services;

public class ServiceMapper {
    public static SearchResultEntryResponse toSearchResponse(Services service) {
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

        return entry;
    }
}
