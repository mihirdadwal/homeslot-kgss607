package com.kgs.homeslot.module.property.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "properties")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "property_type", nullable = false)
    private String propertyType; // APARTMENT, VILLA, PLOT, PENTHOUSE, COMMERCIAL

    @Column(name = "listing_type", nullable = false)
    private String listingType; // BUY, RENT

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    private Integer bhk; // 1, 2, 3, 4, 5

    private Integer bathrooms;

    @Column(name = "area_sqft")
    private Double areaSqft;

    private String address;

    @Column(nullable = false)
    private String city;

    private String state;

    @Column(name = "zip_code")
    private String zipCode;

    private Double latitude;

    private Double longitude;

    @Column(nullable = false)
    private String status; // READY_TO_MOVE, UNDER_CONSTRUCTION, NEW_LAUNCH

    @Column(columnDefinition = "TEXT")
    private String amenities; // Comma separated list

    @Column(name = "cover_image_url")
    private String coverImageUrl;

    @Column(name = "image_urls", columnDefinition = "TEXT")
    private String imageUrls; // Comma separated list of image links

    @Column(name = "brochure_url")
    private String brochureUrl;

    @Column(name = "builder_id")
    private Long builderId;

    @Column(name = "builder_name")
    private String builderName;

    @Column(name = "avg_rating")
    @Builder.Default
    private Double avgRating = 0.0;

    @Column(name = "review_count")
    @Builder.Default
    private Integer reviewCount = 0;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
