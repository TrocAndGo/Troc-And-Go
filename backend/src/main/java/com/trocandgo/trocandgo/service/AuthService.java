package com.trocandgo.trocandgo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.trocandgo.trocandgo.entity.Users;
import com.trocandgo.trocandgo.exception.NotAuthenticatedException;

@Service
public class AuthService {
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

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
}
