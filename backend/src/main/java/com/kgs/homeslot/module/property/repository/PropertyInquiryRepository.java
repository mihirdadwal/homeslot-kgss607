package com.kgs.homeslot.module.property.repository;

import com.kgs.homeslot.module.property.entity.PropertyInquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyInquiryRepository extends JpaRepository<PropertyInquiry, Long> {
    List<PropertyInquiry> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<PropertyInquiry> findByBuilderIdOrderByCreatedAtDesc(Long builderId);
    long countByUserId(Long userId);
}
