package com.kgs.homeslot.module.property.repository;

import com.kgs.homeslot.module.property.entity.RecentlyViewed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecentlyViewedRepository extends JpaRepository<RecentlyViewed, Long> {
    List<RecentlyViewed> findTop10ByUserIdOrderByViewedAtDesc(Long userId);
    Optional<RecentlyViewed> findByUserIdAndPropertyId(Long userId, Long propertyId);
    void deleteByUserId(Long userId);
}
