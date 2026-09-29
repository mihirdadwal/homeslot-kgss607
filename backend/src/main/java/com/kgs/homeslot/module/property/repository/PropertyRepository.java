package com.kgs.homeslot.module.property.repository;

import com.kgs.homeslot.module.property.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long>, JpaSpecificationExecutor<Property> {
    List<Property> findTop6ByOrderByAvgRatingDescCreatedAtDesc();
    List<Property> findByCityIgnoreCase(String city);
    List<Property> findByBuilderIdOrderByCreatedAtDesc(Long builderId);
    long countByBuilderId(Long builderId);
    List<Property> findByApprovalStatusOrderByCreatedAtDesc(String approvalStatus);
    long countByApprovalStatus(String approvalStatus);
}
