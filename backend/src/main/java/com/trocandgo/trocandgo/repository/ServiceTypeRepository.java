package com.trocandgo.trocandgo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.trocandgo.trocandgo.entity.ServiceTypes;
import com.trocandgo.trocandgo.entity.enums.ServiceTypeTitle;

@Repository
public interface ServiceTypeRepository extends JpaRepository<ServiceTypes, Long> {
    Optional<ServiceTypes> findByTitle(ServiceTypeTitle title);
}
