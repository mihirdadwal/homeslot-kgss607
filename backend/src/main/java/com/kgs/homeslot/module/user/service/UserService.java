package com.kgs.homeslot.module.user.service;

import com.kgs.homeslot.module.auth.entity.User;
import com.kgs.homeslot.module.auth.repository.UserRepository;
import com.kgs.homeslot.module.user.dto.UserProfileDto;
import com.kgs.homeslot.module.user.entity.BuilderProfile;
import com.kgs.homeslot.module.user.entity.BuyerProfile;
import com.kgs.homeslot.module.user.repository.BuilderProfileRepository;
import com.kgs.homeslot.module.user.repository.BuyerProfileRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BuyerProfileRepository buyerProfileRepository;
    private final BuilderProfileRepository builderProfileRepository;

    public UserService(UserRepository userRepository,
                       BuyerProfileRepository buyerProfileRepository,
                       BuilderProfileRepository builderProfileRepository) {
        this.userRepository = userRepository;
        this.buyerProfileRepository = buyerProfileRepository;
        this.builderProfileRepository = builderProfileRepository;
    }

    public UserProfileDto getUserProfileByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BuyerProfile buyerProfile = buyerProfileRepository.findByUser(user).orElse(null);
        BuilderProfile builderProfile = builderProfileRepository.findByUser(user).orElse(null);

        return UserProfileDto.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .isEmailVerified(user.isEmailVerified())
                .isPhoneVerified(user.isPhoneVerified())
                .isActive(user.isActive())
                .fullName(buyerProfile != null ? buyerProfile.getFullName() : null)
                .city(buyerProfile != null ? buyerProfile.getCity() : null)
                .companyName(builderProfile != null ? builderProfile.getCompanyName() : null)
                .contactPersonName(builderProfile != null ? builderProfile.getContactPersonName() : null)
                .build();
    }
}
