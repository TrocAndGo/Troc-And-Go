package com.trocandgo.trocandgo.dto.response;

import java.util.Date;

import lombok.Data;

@Data
public class SearchResultEntryResponse {
    private String id;
    private String title;
    private String description;
    private String category;
    private String status;
    private String type;
    private String city;
    private Date creationDate;
    private String createdBy;
    private String creatorProfilePicture;
    private String mail;
    private String phoneNumber;
    private boolean isOwner;
    private boolean isFavorite;
}
