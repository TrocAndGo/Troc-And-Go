package com.trocandgo.trocandgo.dto.request;

import lombok.Value;

@Value
public class SearchRequest {
    private String category;
    private String city;
    private String zipcode;
    private String department;
    private String region;
    private String country;
}
