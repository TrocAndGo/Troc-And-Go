package com.trocandgo.trocandgo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@RequiredArgsConstructor
@IdClass(FavoritesPK.class)
public class Favorites {
    @Id
    @NonNull
    @ManyToOne
    @MapsId
    @JoinColumn
    private Users user;

    @Id
    @NonNull
    @ManyToOne
    @MapsId
    @JoinColumn
    private Services service;
}
