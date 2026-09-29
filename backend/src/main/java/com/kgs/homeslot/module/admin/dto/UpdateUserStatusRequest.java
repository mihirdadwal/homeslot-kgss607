package com.kgs.homeslot.module.admin.dto;

import com.kgs.homeslot.module.auth.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserStatusRequest {
    private Boolean active;
    private RoleType role;
}
