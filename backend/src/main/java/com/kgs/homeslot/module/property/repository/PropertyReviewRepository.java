package com.kgs.homeslot.module.property.repository;

import com.kgs.homeslot.module.property.entity.PropertyReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyReviewRepository extends JpaRepository<PropertyReview, Long> {
    List<PropertyReview> findByPropertyIdOrderByCreatedAtDesc(Long propertyId);
    
    @Query("SELECT AVG(r.rating) FROM PropertyReview r WHERE r.property.id = :propertyId")
    Double getAverageRatingForProperty(Long propertyId);

    @Query("SELECT COUNT(r) FROM PropertyReview r WHERE r.property.id = :propertyId")
    Integer getReviewCountForProperty(Long propertyId);
}
