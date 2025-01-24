package com.trocandgo.trocandgo.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.trocandgo.trocandgo.entity.Adresses;
import com.trocandgo.trocandgo.entity.Services;
import com.trocandgo.trocandgo.entity.Users;

public interface ServiceRepository extends JpaRepository<Services, UUID>, JpaSpecificationExecutor<Services> {
    boolean existsByAdress(Adresses adress);
    Page<Services> findAllByCreatedBy(Users user, Pageable pageable);
}
