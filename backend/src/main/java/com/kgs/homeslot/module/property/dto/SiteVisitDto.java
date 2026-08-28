package com.kgs.homeslot.module.property.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SiteVisitDto {
    private Long id;
    private Long propertyId;
    private String propertyTitle;
    private String propertyAddress;
    private String coverImageUrl;
    private LocalDate visitDate;
    private String timeSlot;
    private String status;
    private String notes;
    private LocalDateTime createdAt;
}
