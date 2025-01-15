package com.trocandgo.trocandgo.dto.response;

import lombok.Value;

@Value
public class AuthLoginResponse {
    private String token;
    private String message;
}
