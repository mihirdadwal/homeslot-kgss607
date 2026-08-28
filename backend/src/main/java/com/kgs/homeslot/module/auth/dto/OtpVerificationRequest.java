package com.kgs.homeslot.module.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OtpVerificationRequest {

    @NotBlank(message = "Target (email/phone) is required")
    private String target;

    @NotBlank(message = "OTP code is required")
    private String otpCode;

    @NotBlank(message = "Purpose is required")
    private String purpose; // e.g. VERIFY_PHONE, VERIFY_EMAIL, RESET_PASSWORD
}
