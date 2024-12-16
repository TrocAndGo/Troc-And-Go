package com.trocandgo.trocandgo.dto.request;

import java.util.Set;

import lombok.Value;

@Value
public class SignupRequest {
    private String username;
    private String email;
    private String password;
    private Set<String> roles;
}
