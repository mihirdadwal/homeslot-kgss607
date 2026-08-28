package com.kgs.homeslot.module.user.dto;

import com.kgs.homeslot.module.auth.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {

    private Long userId;
    private String email;
    private String phone;
    private RoleType role;
    private boolean isEmailVerified;
    private boolean isPhoneVerified;
    private boolean isActive;
    private String fullName;
    private String companyName;
    private String contactPersonName;
    private String city;
}
