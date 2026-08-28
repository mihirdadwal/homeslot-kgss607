package com.kgs.homeslot.module.user.repository;

import com.kgs.homeslot.module.auth.entity.User;
import com.kgs.homeslot.module.user.entity.BuilderProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BuilderProfileRepository extends JpaRepository<BuilderProfile, Long> {
    Optional<BuilderProfile> findByUser(User user);
}
