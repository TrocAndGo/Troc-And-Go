package com.trocandgo.trocandgo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class Adresses {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NonNull
    @Column(length = 255)
    private String address;

    @NonNull
    @Column(length = 100)
    private String city;

    @NonNull
    @Column(length = 20)
    private String zipCode;

    @NonNull
    @Column(length = 100)
    private String department;

    @NonNull
    @Column(length = 100)
    private String region;

    @NonNull
    @Column(length = 100)
    private String country;
}
