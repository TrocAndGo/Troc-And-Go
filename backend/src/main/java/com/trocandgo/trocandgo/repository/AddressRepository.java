package com.trocandgo.trocandgo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trocandgo.trocandgo.model.Adresses;

public interface AddressRepository extends JpaRepository<Adresses, Long> {}
