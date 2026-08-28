package com.kgs.homeslot.module.property.repository;

import com.kgs.homeslot.module.property.entity.SiteVisitSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SiteVisitScheduleRepository extends JpaRepository<SiteVisitSchedule, Long> {
    List<SiteVisitSchedule> findByUserIdOrderByVisitDateAsc(Long userId);
    long countByUserId(Long userId);
}
