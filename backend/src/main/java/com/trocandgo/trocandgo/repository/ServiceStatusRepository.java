package com.trocandgo.trocandgo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.trocandgo.trocandgo.model.ServiceStatuses;
import com.trocandgo.trocandgo.model.enums.ServiceStatusTitle;

@Repository
public interface ServiceStatusRepository extends JpaRepository<ServiceStatuses, Long> {
    ServiceStatuses getByTitle(ServiceStatusTitle title);
}
