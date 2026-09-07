package com.kgs.homeslot.module.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuilderProfileDto {
    private Long id;
    private Long userId;
    private String email;
    private String phone;
    private String companyName;
    private String contactPersonName;
    private String businessLicenseNumber;
}
