package com.kgs.homeslot.module.auth.repository;

import com.kgs.homeslot.module.auth.entity.RefreshToken;
import com.kgs.homeslot.module.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    int deleteByUser(User user);
}
