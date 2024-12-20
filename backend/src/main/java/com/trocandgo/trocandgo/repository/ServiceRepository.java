package com.trocandgo.trocandgo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trocandgo.trocandgo.model.Adresses;
import com.trocandgo.trocandgo.model.Services;

public interface ServiceRepository extends JpaRepository<Services, Long> {
    boolean existsByAdress(Adresses adress);

    List<Services> findFirst25ByOrderByCreationDateDesc();
}
