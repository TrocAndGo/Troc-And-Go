package com.trocandgo.trocandgo.model;

import com.trocandgo.trocandgo.model.enums.RoleName;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@NoArgsConstructor
@RequiredArgsConstructor
@ToString
public class Roles {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private long id;

   @NonNull
   @Enumerated(EnumType.STRING)
   private RoleName name;
}
