package com.trocandgo.trocandgo;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.trocandgo.trocandgo.entity.ServiceCategories;
import com.trocandgo.trocandgo.repository.ServiceCategoryRepository;

import jakarta.annotation.PostConstruct;

@Component
public class DefaultDataInitializer {

    @Autowired
    private ServiceCategoryRepository serviceCategoriesRepository;

    @PostConstruct
    public void initializeDefaultData() {
        initializeDefaultServiceCategories();
    }

    private void initializeDefaultServiceCategories() {
        List<ServiceCategories> defaultCategories = Arrays.asList(
            new ServiceCategories("Jardinage"),
            new ServiceCategories("Bricolage"),
            new ServiceCategories("Massage")
        );

        // serviceCategoriesRepository.deleteAll();
        defaultCategories.forEach(category -> {
            if (!serviceCategoriesRepository.existsByTitle(category.getTitle())) {
                serviceCategoriesRepository.save(category);
            }
        });
    }
}
