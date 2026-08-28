package com.kgs.homeslot.module.property.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateSiteVisitRequest {
    @NotNull(message = "Property ID is required")
    private Long propertyId;

    @NotNull(message = "Visit date is required")
    @FutureOrPresent(message = "Visit date must be today or in the future")
    private LocalDate visitDate;

    @NotBlank(message = "Time slot is required")
    private String timeSlot; // e.g. "10:00 AM - 11:00 AM"

    private String notes;
}
