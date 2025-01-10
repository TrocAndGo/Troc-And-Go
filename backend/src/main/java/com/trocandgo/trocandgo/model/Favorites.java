package com.trocandgo.trocandgo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
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
    @OneToOne
    @MapsId
    @JoinColumn
    private Users user;

    @Id
    @NonNull
    @OneToOne
    @MapsId
    @JoinColumn
    private Services service;
}
