package com.trocandgo.trocandgo.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.trocandgo.trocandgo.dto.request.SearchRequest;
import com.trocandgo.trocandgo.model.Adresses;
import com.trocandgo.trocandgo.model.ServiceCategories;
import com.trocandgo.trocandgo.model.Services;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;

public class ServiceSpecification {
    public static Specification<Services> buildSearchSpecificationFromRequest(SearchRequest filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<Services, Adresses> adressJoin = root.join("adress");

            if (StringUtils.hasText(filter.getCategory())) {
                Join<Services, ServiceCategories> categoryJoin = root.join("category");
                predicates.add(criteriaBuilder.equal(categoryJoin.get("title"), filter.getCategory()));
            }

            if (StringUtils.hasText(filter.getCity())) {
                predicates.add(criteriaBuilder.equal(adressJoin.get("city"), filter.getCity()));
            }

            if (StringUtils.hasText(filter.getZipcode())) {
                predicates.add(criteriaBuilder.equal(adressJoin.get("zipCode"), filter.getZipcode()));
            }

            if (StringUtils.hasText(filter.getDepartment())) {
                predicates.add(criteriaBuilder.equal(adressJoin.get("department"), filter.getDepartment()));
            }

            if (StringUtils.hasText(filter.getRegion())) {
                predicates.add(criteriaBuilder.equal(adressJoin.get("region"), filter.getRegion()));
            }

            if (StringUtils.hasText(filter.getCountry())) {
                predicates.add(criteriaBuilder.equal(adressJoin.get("country"), filter.getCountry()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
