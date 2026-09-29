package com.kgs.homeslot.module.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuilderVerificationRequest {
    @NotBlank(message = "Verification status is required (VERIFIED or REJECTED)")
    private String verificationStatus;
    private String remarks;
}
