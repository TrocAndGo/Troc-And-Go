package com.trocandgo.trocandgo.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.trocandgo.trocandgo.entity.Adresses;
import com.trocandgo.trocandgo.entity.Services;

public interface ServiceRepository extends JpaRepository<Services, UUID>, JpaSpecificationExecutor<Services> {
    boolean existsByAdress(Adresses adress);
}
