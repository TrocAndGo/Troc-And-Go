package com.trocandgo.trocandgo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trocandgo.trocandgo.model.Favorites;
import com.trocandgo.trocandgo.model.FavoritesPK;
import com.trocandgo.trocandgo.model.Services;
import com.trocandgo.trocandgo.model.Users;

public interface FavoriteRepository extends JpaRepository<Favorites, FavoritesPK> {
    boolean existsByUserAndService(Users user, Services service);
}
