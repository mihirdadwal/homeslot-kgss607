package com.kgs.homeslot.module.property.service;

import com.kgs.homeslot.module.auth.entity.User;
import com.kgs.homeslot.module.auth.repository.UserRepository;
import com.kgs.homeslot.module.property.dto.*;
import com.kgs.homeslot.module.property.entity.*;
import com.kgs.homeslot.module.property.repository.*;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final FavoritePropertyRepository favoriteRepository;
    private final RecentlyViewedRepository recentlyViewedRepository;
    private final PropertyReviewRepository reviewRepository;
    private final UserRepository userRepository;

    public PropertyService(PropertyRepository propertyRepository,
                           FavoritePropertyRepository favoriteRepository,
                           RecentlyViewedRepository recentlyViewedRepository,
                           PropertyReviewRepository reviewRepository,
                           UserRepository userRepository) {
        this.propertyRepository = propertyRepository;
        this.favoriteRepository = favoriteRepository;
        this.recentlyViewedRepository = recentlyViewedRepository;
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Page<PropertyDto> searchProperties(PropertySearchRequest req, String currentUserEmail) {
        Long userId = getCurrentUserId(currentUserEmail);

        Specification<Property> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (req.getKeyword() != null && !req.getKeyword().trim().isEmpty()) {
                String kw = "%" + req.getKeyword().trim().toLowerCase() + "%";
                Predicate titlePred = cb.like(cb.lower(root.get("title")), kw);
                Predicate descPred = cb.like(cb.lower(root.get("description")), kw);
                Predicate cityPred = cb.like(cb.lower(root.get("city")), kw);
                Predicate addrPred = cb.like(cb.lower(root.get("address")), kw);
                predicates.add(cb.or(titlePred, descPred, cityPred, addrPred));
            }

            if (req.getCity() != null && !req.getCity().trim().isEmpty()) {
                predicates.add(cb.equal(cb.lower(root.get("city")), req.getCity().trim().toLowerCase()));
            }

            if (req.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), req.getMinPrice()));
            }

            if (req.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), req.getMaxPrice()));
            }

            if (req.getPropertyType() != null && !req.getPropertyType().trim().isEmpty()) {
                predicates.add(cb.equal(cb.upper(root.get("propertyType")), req.getPropertyType().trim().toUpperCase()));
            }

            if (req.getBhk() != null && req.getBhk() > 0) {
                predicates.add(cb.equal(root.get("bhk"), req.getBhk()));
            }

            if (req.getStatus() != null && !req.getStatus().trim().isEmpty()) {
                predicates.add(cb.equal(cb.upper(root.get("status")), req.getStatus().trim().toUpperCase()));
            }

            if (req.getListingType() != null && !req.getListingType().trim().isEmpty()) {
                predicates.add(cb.equal(cb.upper(root.get("listingType")), req.getListingType().trim().toUpperCase()));
            }

            if (req.getAmenity() != null && !req.getAmenity().trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("amenities")), "%" + req.getAmenity().trim().toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        if ("price_asc".equalsIgnoreCase(req.getSortBy())) {
            sort = Sort.by(Sort.Direction.ASC, "price");
        } else if ("price_desc".equalsIgnoreCase(req.getSortBy())) {
            sort = Sort.by(Sort.Direction.DESC, "price");
        } else if ("rating_desc".equalsIgnoreCase(req.getSortBy())) {
            sort = Sort.by(Sort.Direction.DESC, "avgRating");
        }

        Pageable pageable = PageRequest.of(req.getPage() != null ? req.getPage() : 0,
                req.getSize() != null ? req.getSize() : 12, sort);

        Page<Property> page = propertyRepository.findAll(spec, pageable);
        
        Set<Long> favoriteIds = userId != null ? getFavoritePropertyIds(userId) : Collections.emptySet();

        return page.map(p -> mapToDto(p, favoriteIds.contains(p.getId())));
    }

    @Transactional
    public PropertyDto getPropertyById(Long id, String currentUserEmail) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Property not found with ID: " + id));

        Long userId = getCurrentUserId(currentUserEmail);
        boolean isFav = false;

        if (userId != null) {
            isFav = favoriteRepository.existsByUserIdAndPropertyId(userId, id);

            // Record into recently viewed table
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                Optional<RecentlyViewed> existingRv = recentlyViewedRepository.findByUserIdAndPropertyId(userId, id);
                if (existingRv.isPresent()) {
                    RecentlyViewed rv = existingRv.get();
                    rv.setViewedAt(LocalDateTime.now());
                    recentlyViewedRepository.save(rv);
                } else {
                    RecentlyViewed rv = RecentlyViewed.builder()
                            .user(user)
                            .property(property)
                            .viewedAt(LocalDateTime.now())
                            .build();
                    recentlyViewedRepository.save(rv);
                }
            }
        }

        return mapToDto(property, isFav);
    }

    @Transactional(readOnly = true)
    public List<PropertyDto> getFeaturedProperties(String currentUserEmail) {
        Long userId = getCurrentUserId(currentUserEmail);
        Set<Long> favoriteIds = userId != null ? getFavoritePropertyIds(userId) : Collections.emptySet();

        List<Property> featured = propertyRepository.findTop6ByOrderByAvgRatingDescCreatedAtDesc();
        return featured.stream().map(p -> mapToDto(p, favoriteIds.contains(p.getId()))).collect(Collectors.toList());
    }

    @Transactional
    public PropertyReviewDto addReview(Long propertyId, CreateReviewRequest request, String currentUserEmail) {
        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + currentUserEmail));

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Property not found with ID: " + propertyId));

        PropertyReview review = PropertyReview.builder()
                .user(user)
                .userFullName(user.getEmail().split("@")[0]) // Default display name
                .property(property)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        PropertyReview saved = reviewRepository.save(review);

        // Recalculate average rating and total review count
        Double avgRating = reviewRepository.getAverageRatingForProperty(propertyId);
        Integer count = reviewRepository.getReviewCountForProperty(propertyId);
        property.setAvgRating(avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 0.0);
        property.setReviewCount(count != null ? count : 0);
        propertyRepository.save(property);

        return PropertyReviewDto.builder()
                .id(saved.getId())
                .propertyId(propertyId)
                .userId(user.getId())
                .userFullName(saved.getUserFullName())
                .rating(saved.getRating())
                .comment(saved.getComment())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public List<PropertyReviewDto> getPropertyReviews(Long propertyId) {
        List<PropertyReview> reviews = reviewRepository.findByPropertyIdOrderByCreatedAtDesc(propertyId);
        return reviews.stream().map(r -> PropertyReviewDto.builder()
                .id(r.getId())
                .propertyId(r.getProperty().getId())
                .userId(r.getUser().getId())
                .userFullName(r.getUserFullName())
                .rating(r.getRating())
                .comment(r.getComment())
                .createdAt(r.getCreatedAt())
                .build()).collect(Collectors.toList());
    }

    public Set<Long> getFavoritePropertyIds(Long userId) {
        return favoriteRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(f -> f.getProperty().getId())
                .collect(Collectors.toSet());
    }

    private Long getCurrentUserId(String email) {
        if (email == null) return null;
        return userRepository.findByEmail(email).map(User::getId).orElse(null);
    }

    public PropertyDto mapToDto(Property p, boolean isFavorite) {
        return PropertyDto.builder()
                .id(p.getId())
                .title(p.getTitle())
                .description(p.getDescription())
                .propertyType(p.getPropertyType())
                .listingType(p.getListingType())
                .price(p.getPrice())
                .bhk(p.getBhk())
                .bathrooms(p.getBathrooms())
                .areaSqft(p.getAreaSqft())
                .address(p.getAddress())
                .city(p.getCity())
                .state(p.getState())
                .zipCode(p.getZipCode())
                .latitude(p.getLatitude())
                .longitude(p.getLongitude())
                .status(p.getStatus())
                .amenities(p.getAmenities())
                .coverImageUrl(p.getCoverImageUrl())
                .imageUrls(p.getImageUrls())
                .brochureUrl(p.getBrochureUrl() != null ? p.getBrochureUrl() : "/api/v1/properties/" + p.getId() + "/brochure")
                .builderId(p.getBuilderId())
                .builderName(p.getBuilderName())
                .avgRating(p.getAvgRating())
                .reviewCount(p.getReviewCount())
                .isFavorite(isFavorite)
                .createdAt(p.getCreatedAt())
                .build();
    }
}
