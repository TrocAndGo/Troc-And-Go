package com.trocandgo.trocandgo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.trocandgo.trocandgo.model.Account;
import com.trocandgo.trocandgo.repository.AccountRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    AccountRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("No user found with username: " + username));

        return new UserDetailsImpl(account);
    }
}
