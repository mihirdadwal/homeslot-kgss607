package com.kgs.homeslot.module.property.repository;

import com.kgs.homeslot.module.property.entity.FavoriteProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoritePropertyRepository extends JpaRepository<FavoriteProperty, Long> {
    List<FavoriteProperty> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<FavoriteProperty> findByUserIdAndPropertyId(Long userId, Long propertyId);
    boolean existsByUserIdAndPropertyId(Long userId, Long propertyId);
    void deleteByUserIdAndPropertyId(Long userId, Long propertyId);
    long countByUserId(Long userId);
}
