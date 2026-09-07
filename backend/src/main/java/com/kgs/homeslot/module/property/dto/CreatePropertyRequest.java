package com.kgs.homeslot.module.property.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePropertyRequest {

    @NotBlank(message = "Property title is required")
    private String title;

    private String description;

    @NotBlank(message = "Property type is required")
    private String propertyType; // APARTMENT, VILLA, PLOT, PENTHOUSE, COMMERCIAL

    @NotBlank(message = "Listing type is required")
    private String listingType; // BUY, RENT

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    private Integer bhk;
    private Integer bathrooms;
    private Double areaSqft;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "City is required")
    private String city;

    private String state;
    private String zipCode;
    private Double latitude;
    private Double longitude;

    @NotBlank(message = "Status is required")
    private String status; // READY_TO_MOVE, UNDER_CONSTRUCTION, NEW_LAUNCH

    private String amenities;
    private String coverImageUrl;
    private String imageUrls;
    private String brochureUrl;
}
