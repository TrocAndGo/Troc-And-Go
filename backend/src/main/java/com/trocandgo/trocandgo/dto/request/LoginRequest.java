package com.trocandgo.trocandgo.dto.request;

import lombok.Value;

@Value
public class LoginRequest {
    private String username;
    private String password;
}
