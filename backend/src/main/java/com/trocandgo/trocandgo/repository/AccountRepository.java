package com.trocandgo.trocandgo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.trocandgo.trocandgo.model.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long>{
    Optional<Account> findByUsername(String username);

    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}
