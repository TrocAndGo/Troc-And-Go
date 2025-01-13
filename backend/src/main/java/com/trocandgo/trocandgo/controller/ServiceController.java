package com.trocandgo.trocandgo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trocandgo.trocandgo.dto.request.CreateReviewRequest;
import com.trocandgo.trocandgo.dto.request.CreateServiceRequest;
import com.trocandgo.trocandgo.dto.request.SearchRequest;
import com.trocandgo.trocandgo.model.Reviews;
import com.trocandgo.trocandgo.model.Services;
import com.trocandgo.trocandgo.service.ServiceService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/services")
public class ServiceController {
    @Autowired
    private ServiceService serviceService;

    @GetMapping("hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello");
    }

    @GetMapping("")
    public Page<Services> search(SearchRequest request,
            @SortDefault(sort = "creationDate", direction = Direction.DESC) @PageableDefault(size = 20) Pageable pageable) {
        var servicePage = serviceService.findServicesPaginated(request, pageable);

        return servicePage;
    }

    @PostMapping("")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Services> createService(@Valid @RequestBody CreateServiceRequest request) {
        var service = serviceService.createService(request);

        return ResponseEntity.ok(service);
    }

    @GetMapping("{id}")
    public ResponseEntity<Services> getService(@PathVariable(value = "id") String serviceId) {
        var service = serviceService.getServiceById(serviceId);

        return ResponseEntity.ok(service);
    }

    @GetMapping("{id}/reviews")
    public Page<Reviews> getReviewsPaginated(@PathVariable(value = "id") String serviceId,
            @SortDefault(sort = "createdAt", direction = Direction.DESC) @PageableDefault(size = 20) Pageable pageable) {
        var reviewPage = serviceService.findServiceReviewsPaginated(serviceId, pageable);

        return reviewPage;
    }

    @PostMapping("{id}/reviews")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Reviews> createReview(@PathVariable(value = "id") String serviceId, @Valid @RequestBody CreateReviewRequest request) {
        var review = serviceService.createServiceReview(serviceId, request);

        return ResponseEntity.ok(review);
    }
}
