package com.trocandgo.trocandgo.controller;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import com.trocandgo.trocandgo.dto.request.SearchRequest;
import com.trocandgo.trocandgo.model.Reviews;
import com.trocandgo.trocandgo.model.Services;
import com.trocandgo.trocandgo.repository.ReviewRepository;
import com.trocandgo.trocandgo.repository.ServiceRepository;
import com.trocandgo.trocandgo.specification.ServiceSpecification;

@RestController
@RequestMapping("/api/v1/public")
public class PublicController {
    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @GetMapping("hello")
    public ResponseEntity<?> hello() {
        return ResponseEntity.ok("Hello");
    }

    @GetMapping("services")
    public Page<Services> search(SearchRequest request,
            @SortDefault(sort = "creationDate", direction = Direction.DESC) @PageableDefault(size = 20) Pageable pageable) {
        var specification = ServiceSpecification.buildSearchSpecificationFromRequest(request);
        return serviceRepository.findAll(specification, pageable);
    }

    @GetMapping("services/{id}")
    public ResponseEntity<?> getService(@PathVariable(value = "id") UUID serviceId) throws ResponseStatusException {
        Optional<Services> service = serviceRepository.findById(serviceId);

        if (service.isPresent())
            return ResponseEntity.ok(service.get());

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Service not found");
    }

    @GetMapping("services/{id}/reviews")
    public Page<Reviews> getReviews(@PathVariable(value = "id") UUID serviceId,
            @SortDefault(sort = "createdAt", direction = Direction.DESC) @PageableDefault(size = 20) Pageable pageable) throws ResponseStatusException {
        Optional<Services> service = serviceRepository.findById(serviceId);

        if (!service.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Service not found");

        return reviewRepository.findAllByService(service.get(), pageable);
    }
}
