package com.trocandgo.trocandgo.dto.response;

import lombok.Value;

@Value
public class AdressFiltersResponse {
    private String[] regions;
    private String[] departments;
    private String[] cities;
}
