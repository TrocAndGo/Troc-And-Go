package com.trocandgo.trocandgo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.trocandgo.trocandgo.model.Services;
import com.trocandgo.trocandgo.repository.ServiceRepository;

@RestController
@RequestMapping("/api/v1/public")
public class PublicController {
    @Autowired
    private ServiceRepository serviceRepository;

    @GetMapping("hello")
    public ResponseEntity<?> hello() {
        return ResponseEntity.ok("Hello");
    }

    @GetMapping("services")
    public List<Services> latestServices(@RequestParam(name = "limit", defaultValue = "25") int limit) {
        return serviceRepository.findAllByOrderByCreationDateDesc(Pageable.ofSize(limit)).getContent();
    }
}
