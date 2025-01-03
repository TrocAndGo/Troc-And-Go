package com.trocandgo.trocandgo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.trocandgo.trocandgo.model.Users;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByName(String username);

    Boolean existsByName(String username);
    Boolean existsByEmail(String email);
}
