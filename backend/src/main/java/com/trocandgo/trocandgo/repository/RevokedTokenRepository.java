package com.trocandgo.trocandgo.repository;

import com.trocandgo.trocandgo.model.RevokedToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RevokedTokenRepository extends CrudRepository<RevokedToken, String> {
    // Vous pouvez ajouter des méthodes personnalisées si nécessaire, par exemple :
    // Optional<RevokedToken> findByToken(String token);
}
