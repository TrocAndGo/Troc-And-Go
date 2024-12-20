package com.trocandgo.trocandgo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.trocandgo.trocandgo.model.ServiceStatuses;
import com.trocandgo.trocandgo.model.ServiceTypes;
import com.trocandgo.trocandgo.model.enums.ServiceTypeTitle;

@Repository
public interface ServiceStatusRepository extends JpaRepository<ServiceStatuses, Long> {
    ServiceTypes getByTitle(ServiceTypeTitle title);
}
