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
public class PropertyApprovalRequest {
    @NotBlank(message = "Approval status is required (APPROVED or REJECTED)")
    private String approvalStatus;
    private String rejectionReason;
}
