package com.kgs.homeslot.module.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuyerProfileDto {
    private Long id;
    private Long userId;
    private String email;
    private String phone;
    private String fullName;
    private String city;
    private BigDecimal budgetMin;
    private BigDecimal budgetMax;
    private String preferredPropertyType;
    private Integer preferredBhk;
}
