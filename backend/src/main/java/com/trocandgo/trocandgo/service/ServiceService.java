package com.trocandgo.trocandgo.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.trocandgo.trocandgo.dto.request.CreateReviewRequest;
import com.trocandgo.trocandgo.dto.request.CreateServiceRequest;
import com.trocandgo.trocandgo.dto.request.SearchRequest;
import com.trocandgo.trocandgo.entity.Reviews;
import com.trocandgo.trocandgo.entity.ReviewsPK;
import com.trocandgo.trocandgo.entity.ServiceCategories;
import com.trocandgo.trocandgo.entity.ServiceStatuses;
import com.trocandgo.trocandgo.entity.ServiceTypes;
import com.trocandgo.trocandgo.entity.Services;
import com.trocandgo.trocandgo.exception.InvalidPageableException;
import com.trocandgo.trocandgo.exception.InvalidServiceCategoryException;
import com.trocandgo.trocandgo.exception.InvalidUUIDException;
import com.trocandgo.trocandgo.exception.NotAuthenticatedException;
import com.trocandgo.trocandgo.exception.ReviewAlreadyExistsException;
import com.trocandgo.trocandgo.exception.SelfReviewException;
import com.trocandgo.trocandgo.exception.ServiceNotFoundException;
import com.trocandgo.trocandgo.repository.AddressRepository;
import com.trocandgo.trocandgo.repository.ReviewRepository;
import com.trocandgo.trocandgo.repository.ServiceCategoryRepository;
import com.trocandgo.trocandgo.repository.ServiceRepository;
import com.trocandgo.trocandgo.repository.ServiceStatusRepository;
import com.trocandgo.trocandgo.repository.ServiceTypeRepository;
import com.trocandgo.trocandgo.specification.ServiceSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServiceService {
    @Autowired
    private final AuthService authService;

    @Autowired
    private final ServiceRepository serviceRepository;

    @Autowired
    private final ServiceCategoryRepository serviceCategoryRepository;

    @Autowired
    private final ServiceTypeRepository serviceTypeRepository;

    @Autowired
    private final ServiceStatusRepository serviceStatusRepository;

    @Autowired
    private final ReviewRepository reviewRepository;

    @Autowired
    private final AddressRepository adressRepository;

    /**
     * Retrieves a service by its unique id.
     *
     * @param serviceId the string representation of the service's UUID.
     *                  Must not be null or an invalid UUID format.
     * @return the {@link Services} associated with the provided UUID.
     * @throws InvalidUUIDException if the provided {@code serviceId} is null or cannot be converted to a valid UUID.
     * @throws ServiceNotFoundException if no service is found with the provided UUID.
     */
    public Services getServiceById(String serviceId) {
        UUID uuid;

        try {
            uuid = UUID.fromString(serviceId != null ? serviceId : "");
        } catch (IllegalArgumentException e) {
            throw new InvalidUUIDException(serviceId);
        }

        var service = serviceRepository.findById(uuid);
        if (!service.isPresent())
            throw new ServiceNotFoundException(serviceId);

        return service.get();
    }

    /**
     * Creates a new service based on the provided request.
     *
     * @param request the {@link CreateServiceRequest} containing details for the new service.
     * @return the newly created {@link Services} object.
     * @throws NotAuthenticatedException if there is no authenticated user or the user is not logged in.
     * @throws InvalidServiceCategoryException if the specified service category ID is invalid or does not exist.
     */
    public Services createService(CreateServiceRequest request) {
        var user = authService.getLoggedInUser();

        var category = serviceCategoryRepository.findById(request.getCategoryId());
        if (!category.isPresent())
            throw new InvalidServiceCategoryException("Service category is invalid: " + request.getCategoryId());

        var type = serviceTypeRepository.findByTitle(request.getType())
                            .orElse(new ServiceTypes(request.getType()));
        var status = serviceStatusRepository.findByTitle(request.getStatus())
                            .orElse(new ServiceStatuses(request.getStatus()));

        var service = new Services(request.getTitle(), user, type, status, category.get(), user.getAddress());
        service.setDescription(request.getDescription());
        service = serviceRepository.save(service);

        return service;
    }

    /**
     * Retrieves a paginated list of services based on the given search criteria.
     *
     * @param request  the {@link SearchRequest} containing the search criteria.
     * @param pageable the pagination and sorting information. Must not be null.
     * @return a {@link Page} containing {@link Services} that match the search criteria.
     * @throws InvalidPageableException if the {@code pageable} parameter is null.
     */
    public Page<Services> findServicesPaginated(SearchRequest request, Pageable pageable) {
        if (pageable == null)
            throw new InvalidPageableException("Pageable must not be null.");

        var specification = ServiceSpecification.buildSearchSpecificationFromRequest(request);
        return serviceRepository.findAll(specification, pageable);
    }

    /**
     * Retrieves a paginated list of reviews for a specific service.
     *
     * @param serviceId the string representation of the service's UUID.
     *                  Must correspond to an existing service.
     * @param pageable  the pagination and sorting information. Must not be null.
     * @return a {@link Page} containing {@link Reviews} for the specified service.
     * @throws InvalidPageableException if the provided {@code pageable} is null.
     * @throws InvalidUUIDException if the provided {@code serviceId} is null or cannot be converted to a valid UUID.
     * @throws ServiceNotFoundException if no service is found with the provided UUID.
     */
    public Page<Reviews> findServiceReviewsPaginated(String serviceId, Pageable pageable) {
        if (pageable == null)
            throw new InvalidPageableException("Pageable must not be null.");

        var service = getServiceById(serviceId);

        return reviewRepository.findAllByService(service, pageable);
    }

    /**
     * Creates a new review for a specified service.
     *
     * @param serviceId the unique identifier of the service to be reviewed.
     * @param request   the {@link CreateReviewRequest} containing details of the review.
     * @return the newly created {@link Reviews} object.
     * @throws NotAuthenticatedException if there is no authenticated user or the user is not logged in.
     * @throws ServiceNotFoundException if no service is found with the provided ID.
     * @throws SelfReviewException if the logged-in user attempts to review their own service.
     * @throws ReviewAlreadyExistsException if the logged-in user has already reviewed the specified service.
     */
    public Reviews createServiceReview(String serviceId, CreateReviewRequest request) {
        var user = authService.getLoggedInUser();
        var service = getServiceById(serviceId);

        if (service.getCreatedBy() == user)
            throw new SelfReviewException();

        ReviewsPK reviewId = new ReviewsPK(user, service);
        if (reviewRepository.existsById(reviewId))
            throw new ReviewAlreadyExistsException();

        Reviews review = new Reviews(reviewId.getUser(), reviewId.getService(), request.getComment(), request.getRating());
        review = reviewRepository.save(review);

        return review;
    }

    public ServiceCategories[] getCategoryList() {
        return serviceCategoryRepository.findAll().toArray(new ServiceCategories[0]);
    }

    public String[] getRegions() {
        return adressRepository.findAllDistinctRegions().toArray(new String[0]);
    }

    public String[] getDepartments() {
        return adressRepository.findAllDistinctDepartments().toArray(new String[0]);
    }

    public String[] getCities() {
        return adressRepository.findAllDistinctCities().toArray(new String[0]);
    }
}
