package com.trocandgo.trocandgo.service;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.trocandgo.trocandgo.model.Users;

import lombok.Getter;

public class UserDetailsImpl implements UserDetails {

    @Getter
    private final Users user;
    private final List<SimpleGrantedAuthority> authorities;

    public UserDetailsImpl(Users user) {
        this.user = user;
        this.authorities = user.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority(role.getName().name())).toList();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.user.getPassword();
    }

    @Override
    public String getUsername() {
        return this.user.getName();
    }

}
