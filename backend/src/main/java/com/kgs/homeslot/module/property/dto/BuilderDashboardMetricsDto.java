package com.kgs.homeslot.module.property.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuilderDashboardMetricsDto {
    private long totalProperties;
    private long activeListings;
    private long totalInquiries;
    private long pendingInquiries;
    private long totalSiteVisits;
    private long upcomingSiteVisits;
}
