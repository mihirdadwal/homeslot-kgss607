package com.kgs.homeslot.module.property.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyDto {
    private Long id;
    private String title;
    private String description;
    private String propertyType;
    private String listingType;
    private BigDecimal price;
    private Integer bhk;
    private Integer bathrooms;
    private Double areaSqft;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private Double latitude;
    private Double longitude;
    private String status;
    private String amenities;
    private String coverImageUrl;
    private String imageUrls;
    private String brochureUrl;
    private Long builderId;
    private String builderName;
    private Double avgRating;
    private Integer reviewCount;
    private Boolean isFavorite;
    private String approvalStatus;
    private String rejectionReason;
    private LocalDateTime createdAt;
}
