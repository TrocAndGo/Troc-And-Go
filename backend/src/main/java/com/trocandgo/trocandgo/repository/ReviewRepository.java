package com.trocandgo.trocandgo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.trocandgo.trocandgo.entity.Reviews;
import com.trocandgo.trocandgo.entity.ReviewsPK;
import com.trocandgo.trocandgo.entity.Services;

public interface ReviewRepository extends JpaRepository<Reviews, ReviewsPK> {
    Page<Reviews> findAllByService(Services service, Pageable pageable);
}
