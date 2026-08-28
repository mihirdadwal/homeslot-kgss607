package com.kgs.homeslot.module.property.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PropertySearchRequest {
    private String keyword;
    private String city;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String propertyType;
    private Integer bhk;
    private String status;
    private String listingType;
    private String amenity;
    private String sortBy; // price_asc, price_desc, rating_desc, newest
    private Integer page = 0;
    private Integer size = 12;
}
