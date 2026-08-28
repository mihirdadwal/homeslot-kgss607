package com.kgs.homeslot.module.user.repository;

import com.kgs.homeslot.module.auth.entity.User;
import com.kgs.homeslot.module.user.entity.BuyerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BuyerProfileRepository extends JpaRepository<BuyerProfile, Long> {
    Optional<BuyerProfile> findByUser(User user);
    Optional<BuyerProfile> findByUserId(Long userId);
}
