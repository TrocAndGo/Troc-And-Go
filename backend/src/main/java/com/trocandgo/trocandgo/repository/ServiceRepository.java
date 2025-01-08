package com.trocandgo.trocandgo.repository;

import org.springframework.data.domain.Pageable;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.trocandgo.trocandgo.model.Adresses;
import com.trocandgo.trocandgo.model.Services;

public interface ServiceRepository extends JpaRepository<Services, UUID>, JpaSpecificationExecutor<Services> {
    boolean existsByAdress(Adresses adress);

    Page<Services> findAllByOrderByCreationDateDesc(Pageable pageable);
}
