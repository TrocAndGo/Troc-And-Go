package com.trocandgo.trocandgo.dto.auth;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.trocandgo.trocandgo.model.Account;

import lombok.Getter;

public class UserDetailsAuth implements UserDetails {

    @Getter
    private final Account account;
    private final List<SimpleGrantedAuthority> authorities;

    public UserDetailsAuth(Account account) {
        this.account = account;
        this.authorities = account.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority(role.name())).toList();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.account.getPassword();
    }

    @Override
    public String getUsername() {
        return this.account.getUsername();
    }

}
