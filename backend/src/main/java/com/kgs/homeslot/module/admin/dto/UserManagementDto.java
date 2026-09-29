package com.kgs.homeslot.module.admin.dto;

import com.kgs.homeslot.module.auth.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserManagementDto {
    private Long id;
    private String email;
    private String phone;
    private RoleType role;
    private boolean active;
    private boolean emailVerified;
    private boolean phoneVerified;
    private String nameOrCompany;
    private LocalDateTime createdAt;
}
