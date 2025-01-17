package com.trocandgo.trocandgo.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Value;

@Value
public class SignupRequest {
    @NotEmpty
    private String username;
    @NotEmpty
    private String email;
    @NotEmpty
    private String password;
}
