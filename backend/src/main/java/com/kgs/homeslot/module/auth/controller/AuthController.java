package com.kgs.homeslot.module.auth.controller;

import com.kgs.homeslot.common.response.ApiResponse;
import com.kgs.homeslot.module.auth.dto.*;
import com.kgs.homeslot.module.auth.service.AuthService;
import com.kgs.homeslot.module.user.dto.UserProfileDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register/buyer")
    public ResponseEntity<ApiResponse<UserProfileDto>> registerBuyer(@Valid @RequestBody BuyerRegisterRequest req) {
        UserProfileDto dto = authService.registerBuyer(req);
        return ResponseEntity.ok(ApiResponse.success("Buyer registered successfully. Verification OTP sent.", dto));
    }

    @PostMapping("/register/builder")
    public ResponseEntity<ApiResponse<UserProfileDto>> registerBuilder(@Valid @RequestBody BuilderRegisterRequest req) {
        UserProfileDto dto = authService.registerBuilder(req);
        return ResponseEntity.ok(ApiResponse.success("Builder registered successfully. Verification OTP sent.", dto));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthTokenResponse>> login(@Valid @RequestBody LoginRequest req) {
        AuthTokenResponse response = authService.login(req);
        return ResponseEntity.ok(ApiResponse.success("Authentication successful", response));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<Boolean>> verifyOtp(@Valid @RequestBody OtpVerificationRequest req) {
        boolean verified = authService.verifyOtp(req);
        if (verified) {
            return ResponseEntity.ok(ApiResponse.success("OTP verified successfully", true));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error("Invalid or expired OTP"));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Boolean>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest req) {
        authService.forgotPassword(req);
        return ResponseEntity.ok(ApiResponse.success("Password reset instructions sent to your email", true));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Boolean>> resetPassword(@Valid @RequestBody ResetPasswordRequest req) {
        authService.resetPassword(req);
        return ResponseEntity.ok(ApiResponse.success("Password reset successfully", true));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<AuthTokenResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest req) {
        AuthTokenResponse response = authService.refreshToken(req);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", response));
    }
}
