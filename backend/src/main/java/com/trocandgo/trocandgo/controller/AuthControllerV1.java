package com.trocandgo.trocandgo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trocandgo.trocandgo.dto.request.LoginRequest;
import com.trocandgo.trocandgo.dto.request.SignupRequest;
import com.trocandgo.trocandgo.dto.response.AuthLoginResponse;
import com.trocandgo.trocandgo.dto.response.GenericMessageResponse;
import com.trocandgo.trocandgo.dto.response.RestApiExceptionResponse;
import com.trocandgo.trocandgo.repository.UserRepository;
import com.trocandgo.trocandgo.service.AuthService;
import com.trocandgo.trocandgo.service.JwtService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthControllerV1 {
    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationProvider authenticationProvider;

    @Autowired
    JwtService jwtService;

    @Autowired
    private AuthService authService;

    @Operation(summary = "Authenticate user")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Login successful.",
            content = { @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AuthLoginResponse.class)
            )}
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid username or password.",
            content = { @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RestApiExceptionResponse.class)
            )}
        )
    })
    @PostMapping("/login")
    public ResponseEntity<AuthLoginResponse> login(@Valid @RequestBody LoginRequest request) {
        var token = authService.login(request);

        return ResponseEntity.ok(new AuthLoginResponse(token, "Login successful"));
    }

    @Operation(summary = "Register user")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "User registered successfully.",
            content = { @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GenericMessageResponse.class)
            )}
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Username is already taken.\n\nEmail is already in use.",
            content = { @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RestApiExceptionResponse.class)
            )}
        ),
    })
    @PostMapping("/signup")
    public ResponseEntity<GenericMessageResponse> signup(@Valid @RequestBody SignupRequest signupRequest) {
        authService.createUser(signupRequest);

        return ResponseEntity.ok(new GenericMessageResponse("User registered successfully"));
    }

    @Operation(summary = "Logout user")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully logged out.",
            content = { @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = GenericMessageResponse.class)
            )}
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid token.",
            content = { @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RestApiExceptionResponse.class)
            )}
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Token revocation failed.",
            content = { @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RestApiExceptionResponse.class)
            )}
        )
    })
    @PostMapping("/logout")
    public ResponseEntity<GenericMessageResponse> logout(@RequestHeader("Authorization") String authorizationHeader) {
        authService.logout(authorizationHeader);

        return ResponseEntity.ok(new GenericMessageResponse("Successfully logged out"));
    }

}
