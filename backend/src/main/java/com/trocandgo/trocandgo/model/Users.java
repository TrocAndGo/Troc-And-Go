package com.trocandgo.trocandgo.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@ToString
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NonNull
    @Column(length = 255)
    private String name;

    @NonNull
    @Email
    @Column(length = 255)
    private String email;

    @NonNull
    @Column(length = 255)
    private String password;

    @Column(columnDefinition = "TEXT")
    private String picture;

    @Column(length = 20)
    private String phoneNumber;

    @OneToOne(optional = true, cascade = CascadeType.ALL)
    private Adresses address;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Roles> roles = new HashSet<>();
}
