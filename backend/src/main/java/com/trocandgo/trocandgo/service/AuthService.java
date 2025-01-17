package com.trocandgo.trocandgo.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.trocandgo.trocandgo.dto.request.LoginRequest;
import com.trocandgo.trocandgo.dto.request.SignupRequest;
import com.trocandgo.trocandgo.entity.Roles;
import com.trocandgo.trocandgo.entity.Users;
import com.trocandgo.trocandgo.entity.enums.RoleName;
import com.trocandgo.trocandgo.exception.GenericBadRequestException;
import com.trocandgo.trocandgo.exception.NotAuthenticatedException;
import com.trocandgo.trocandgo.repository.UserRepository;

@Service
public class AuthService {
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Autowired
    private JwtService jwtService;

    /**
     * Retrieves the currently logged-in user.
     *
     * @return the {@link Users} object representing the authenticated user.
     * @throws NotAuthenticatedException if there is no authenticated user or the user is not logged in.
     */
    public Users getLoggedInUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new NotAuthenticatedException();
        }

        var user = (UserDetailsImpl) userDetailsService.loadUserByUsername(authentication.getName());
        return user.getUser();
    }

    /**
     * Creates a new user.
     *
     * @param signupRequest the request object containing the user's information.
     * @return the newly created {@link Users} object.
     * @throws GenericBadRequestException if the username or email is already taken.
     * @throws GenericBadRequestException if the email is already in use.
     */
    public Users createUser(SignupRequest signupRequest) {
        if (userRepository.existsByName(signupRequest.getUsername()))
            throw new GenericBadRequestException("Username is already taken");

        if (userRepository.existsByEmail(signupRequest.getEmail()))
            throw new GenericBadRequestException("Email is already in use");

        Set<Roles> roles = new HashSet<>(Arrays.asList(new Roles(RoleName.ROLE_USER)));

        Users user = new Users(
            signupRequest.getUsername(),
            signupRequest.getEmail(),
            passwordEncoder.encode(signupRequest.getPassword())
        );
        user.setRoles(roles);
        user = userRepository.save(user);

        return user;
    }

    /**
     * Authenticates a user.
     *
     * @param request the request object containing the user's login information.
     * @return the JWT token.
     * @throws GenericBadRequestException if the username or password is invalid.
     */
    public String login(LoginRequest request) {
        try {
            Authentication authentication = authenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
                )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            List<String> roles = userDetails.getAuthorities()
                                    .stream()
                                    .map(grantedAuthority -> grantedAuthority.getAuthority())
                                    .toList();

            String jwtToken = jwtService.generateToken(userDetails.getUsername(), roles);

            return jwtToken;
        } catch (Exception e) {
           throw new GenericBadRequestException("Invalid username or password");
        }
    }

    /**
     * Logs out a user.
     *
     * @param authorizationHeader the authorization header containing the JWT token.
     * @throws GenericBadRequestException if the token is invalid.
     * @throws ResponseStatusException if an error occurs while revoking the token.
     */
    public void logout(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            // Si le token est absent ou mal formaté, retour d'une mauvaise requête
            throw new GenericBadRequestException("Invalid token");
        }

        String token = authorizationHeader.substring(7); // Extraire le token

        try {
            // Révoquer le token JWT en l'ajoutant à Redis (ou un autre store)
            jwtService.revokeToken(token);

            // Effacer l'authentification dans le contexte de sécurité
            SecurityContextHolder.clearContext();

            System.out.println("Authentication cleared");
        } catch (Exception e) {
            // En cas d'erreur, retour d'une réponse interne avec message d'erreur
            System.err.println("Erreur lors de la révocation du token : " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Token revocation failed", e);
        }

    }
}
