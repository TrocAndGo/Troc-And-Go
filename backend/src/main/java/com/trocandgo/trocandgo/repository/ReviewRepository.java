package com.trocandgo.trocandgo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.trocandgo.trocandgo.model.Reviews;
import com.trocandgo.trocandgo.model.ReviewsPK;
import com.trocandgo.trocandgo.model.Services;

public interface ReviewRepository extends JpaRepository<Reviews, ReviewsPK> {
    Page<Reviews> findAllByService(Services service, Pageable pageable);
}
