package com.trocandgo.trocandgo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.trocandgo.trocandgo.entity.Favorites;
import com.trocandgo.trocandgo.entity.FavoritesPK;
import com.trocandgo.trocandgo.entity.Services;
import com.trocandgo.trocandgo.entity.Users;

public interface FavoriteRepository extends JpaRepository<Favorites, FavoritesPK> {
    boolean existsByUserAndService(Users user, Services service);

    Page<Favorites> findByUser(Users user, Pageable pageable);
}
