package com.trocandgo.trocandgo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.trocandgo.trocandgo.model.ServiceCategories;

@Repository
public interface ServiceCategoryRepository extends JpaRepository<ServiceCategories, Long> {
    Optional<ServiceCategories> findByTitle(String title);

    boolean existsByTitle(String title);
}
