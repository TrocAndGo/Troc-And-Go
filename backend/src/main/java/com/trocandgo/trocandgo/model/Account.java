package com.trocandgo.trocandgo.model;

import java.util.Set;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Size(min = 4, max = 25)
    private String username;

    @NonNull
    @NotEmpty
    @Email
    private String email;

    @NonNull
    @NotEmpty
    private String password;

    @NonNull
    @ElementCollection(targetClass = Role.class)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;
}
