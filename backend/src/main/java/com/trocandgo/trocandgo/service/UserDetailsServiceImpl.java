package com.trocandgo.trocandgo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.trocandgo.trocandgo.model.Users;
import com.trocandgo.trocandgo.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = userRepository.findByName(username)
            .orElseThrow(() -> new UsernameNotFoundException("No user found with username: " + username));

        return new UserDetailsImpl(user);
    }
}
