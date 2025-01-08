package com.trocandgo.trocandgo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.trocandgo.trocandgo.dto.request.SearchRequest;
import com.trocandgo.trocandgo.model.Services;
import com.trocandgo.trocandgo.repository.ServiceRepository;
import com.trocandgo.trocandgo.specification.ServiceSpecification;

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
    public List<Services> latestServices(@RequestParam(name = "size", defaultValue = "25") int size) {
        size = Math.clamp(size, 1, 25);
        var sort = Sort.by("creationDate").descending();

        return serviceRepository.findAll(PageRequest.of(0, size, sort)).getContent();
    }

    @GetMapping("search")
    public Page<Services> search(SearchRequest params,
            @SortDefault(sort = "creationDate", direction = Direction.DESC) @PageableDefault(size = 20) Pageable pageable) {
        var specification = ServiceSpecification.searchService(params);
        return serviceRepository.findAll(specification, pageable);
    }
}
