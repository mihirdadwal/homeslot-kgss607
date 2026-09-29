package com.kgs.homeslot.module.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardMetricsDto {
    private long totalUsers;
    private long totalBuyers;
    private long totalBuilders;
    private long totalProperties;
    private long pendingPropertyApprovals;
    private long pendingBuilderVerifications;
    private long totalInquiries;
    private long totalSiteVisits;
}
