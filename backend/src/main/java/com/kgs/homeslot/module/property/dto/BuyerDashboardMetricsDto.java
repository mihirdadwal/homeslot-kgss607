package com.kgs.homeslot.module.property.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuyerDashboardMetricsDto {
    private long totalFavorites;
    private long totalSiteVisits;
    private long totalInquiries;
    private long totalRecentlyViewed;
}
