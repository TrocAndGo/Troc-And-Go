package com.trocandgo.trocandgo.dto.request;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import lombok.Value;

@Value
public class AddFavoriteRequest {
    @JsonSetter(nulls = Nulls.FAIL)
    private long userId; //TODO: Only for debugging purposes until login is implemented

    @JsonSetter(nulls = Nulls.FAIL)
    private UUID serviceId;
}
