package com.trocandgo.trocandgo.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.trocandgo.trocandgo.entity.RevokedToken;

@Repository
public interface RevokedTokenRepository extends CrudRepository<RevokedToken, String> {
    // Vous pouvez ajouter des méthodes personnalisées si nécessaire, par exemple :
    // Optional<RevokedToken> findByToken(String token);
}
