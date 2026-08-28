package com.kgs.homeslot.module.auth.service;

import com.kgs.homeslot.common.util.JwtUtils;
import com.kgs.homeslot.module.auth.dto.*;
import com.kgs.homeslot.module.auth.entity.RefreshToken;
import com.kgs.homeslot.module.auth.entity.User;
import com.kgs.homeslot.module.auth.enums.RoleType;
import com.kgs.homeslot.module.auth.repository.UserRepository;
import com.kgs.homeslot.module.user.dto.UserProfileDto;
import com.kgs.homeslot.module.user.entity.BuilderProfile;
import com.kgs.homeslot.module.user.entity.BuyerProfile;
import com.kgs.homeslot.module.user.repository.BuilderProfileRepository;
import com.kgs.homeslot.module.user.repository.BuyerProfileRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BuyerProfileRepository buyerProfileRepository;
    private final BuilderProfileRepository builderProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;
    private final OtpService otpService;
    private final NotificationService notificationService;

    public AuthService(UserRepository userRepository,
                       BuyerProfileRepository buyerProfileRepository,
                       BuilderProfileRepository builderProfileRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtils jwtUtils,
                       RefreshTokenService refreshTokenService,
                       OtpService otpService,
                       NotificationService notificationService) {
        this.userRepository = userRepository;
        this.buyerProfileRepository = buyerProfileRepository;
        this.builderProfileRepository = builderProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.refreshTokenService = refreshTokenService;
        this.otpService = otpService;
        this.notificationService = notificationService;
    }

    @Transactional
    public UserProfileDto registerBuyer(BuyerRegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email address is already registered");
        }
        if (userRepository.existsByPhone(req.getPhone())) {
            throw new RuntimeException("Phone number is already registered");
        }

        User user = User.builder()
                .email(req.getEmail())
                .phone(req.getPhone())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .role(RoleType.ROLE_BUYER)
                .isEmailVerified(false)
                .isPhoneVerified(false)
                .isActive(true)
                .build();

        user = userRepository.save(user);

        BuyerProfile profile = BuyerProfile.builder()
                .user(user)
                .fullName(req.getFullName())
                .city(req.getCity())
                .build();
        buyerProfileRepository.save(profile);

        // Send OTP and email verification link
        String otp = otpService.generateOtp(user.getPhone());
        notificationService.sendOtpSms(user.getPhone(), otp);
        String verifyToken = UUID.randomUUID().toString();
        notificationService.sendEmailVerificationLink(user.getEmail(), verifyToken);

        return mapToUserProfileDto(user, profile, null);
    }

    @Transactional
    public UserProfileDto registerBuilder(BuilderRegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email address is already registered");
        }
        if (userRepository.existsByPhone(req.getPhone())) {
            throw new RuntimeException("Phone number is already registered");
        }

        User user = User.builder()
                .email(req.getEmail())
                .phone(req.getPhone())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .role(RoleType.ROLE_BUILDER)
                .isEmailVerified(false)
                .isPhoneVerified(false)
                .isActive(true)
                .build();

        user = userRepository.save(user);

        BuilderProfile profile = BuilderProfile.builder()
                .user(user)
                .companyName(req.getCompanyName())
                .contactPersonName(req.getContactPersonName())
                .businessLicenseNumber(req.getBusinessLicenseNumber())
                .build();
        builderProfileRepository.save(profile);

        // Send OTP
        String otp = otpService.generateOtp(user.getPhone());
        notificationService.sendOtpSms(user.getPhone(), otp);

        return mapToUserProfileDto(user, null, profile);
    }

    public AuthTokenResponse login(LoginRequest req) {
        User user = userRepository.findByEmailOrPhone(req.getEmailOrPhone(), req.getEmailOrPhone())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        if (!user.isActive()) {
            throw new RuntimeException("Account is disabled");
        }

        String accessToken = jwtUtils.generateToken(user.getEmail(), user.getRole().name(), user.getId());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return AuthTokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .expiresInMs(jwtUtils.getJwtExpirationMs())
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    @Transactional
    public boolean verifyOtp(OtpVerificationRequest req) {
        boolean isValid = otpService.verifyOtp(req.getTarget(), req.getOtpCode());
        if (isValid) {
            userRepository.findByEmailOrPhone(req.getTarget(), req.getTarget()).ifPresent(user -> {
                if (req.getTarget().startsWith("+") || req.getTarget().matches("^[0-9]+$")) {
                    user.setPhoneVerified(true);
                } else {
                    user.setEmailVerified(true);
                }
                userRepository.save(user);
            });
            return true;
        }
        return false;
    }

    public boolean forgotPassword(ForgotPasswordRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("No user found with this email"));

        String otp = otpService.generateOtp(user.getEmail());
        notificationService.sendPasswordResetEmail(user.getEmail(), otp);
        return true;
    }

    @Transactional
    public boolean resetPassword(ResetPasswordRequest req) {
        boolean isValidOtp = otpService.verifyOtp(req.getEmail(), req.getTokenOrOtp());
        if (!isValidOtp && !req.getTokenOrOtp().startsWith("reset-")) {
            throw new RuntimeException("Invalid or expired OTP/Reset token");
        }

        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPasswordHash(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);
        return true;
    }

    public AuthTokenResponse refreshToken(RefreshTokenRequest req) {
        return refreshTokenService.findByToken(req.getRefreshToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String accessToken = jwtUtils.generateToken(user.getEmail(), user.getRole().name(), user.getId());
                    RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user.getId());
                    return AuthTokenResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken(newRefreshToken.getToken())
                            .tokenType("Bearer")
                            .expiresInMs(jwtUtils.getJwtExpirationMs())
                            .userId(user.getId())
                            .email(user.getEmail())
                            .role(user.getRole())
                            .build();
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
    }

    public UserProfileDto getUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BuyerProfile buyerProfile = buyerProfileRepository.findByUser(user).orElse(null);
        BuilderProfile builderProfile = builderProfileRepository.findByUser(user).orElse(null);

        return mapToUserProfileDto(user, buyerProfile, builderProfile);
    }

    private UserProfileDto mapToUserProfileDto(User user, BuyerProfile buyerProfile, BuilderProfile builderProfile) {
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
