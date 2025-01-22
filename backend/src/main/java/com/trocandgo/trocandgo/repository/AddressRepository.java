package com.trocandgo.trocandgo.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.trocandgo.trocandgo.entity.Adresses;

public interface AddressRepository extends JpaRepository<Adresses, Long> {
    @Query("SELECT DISTINCT a.region FROM Adresses a")
    Set<String> findAllDistinctRegions();

    @Query("SELECT DISTINCT a.department FROM Adresses a")
    Set<String> findAllDistinctDepartments();

    @Query("SELECT DISTINCT a.city FROM Adresses a")
    Set<String> findAllDistinctCities();
}
