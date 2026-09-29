package com.kgs.homeslot.module.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogDto {
    private Long id;
    private String action;
    private String performedByEmail;
    private String targetEntity;
    private Long entityId;
    private String details;
    private LocalDateTime createdAt;
}
