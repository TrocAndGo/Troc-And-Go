package com.trocandgo.trocandgo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.trocandgo.trocandgo.entity.ServiceStatuses;
import com.trocandgo.trocandgo.entity.enums.ServiceStatusTitle;

@Repository
public interface ServiceStatusRepository extends JpaRepository<ServiceStatuses, Long> {
    Optional<ServiceStatuses> findByTitle(ServiceStatusTitle title);
}
