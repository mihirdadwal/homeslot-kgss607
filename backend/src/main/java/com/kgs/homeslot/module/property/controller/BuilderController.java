package com.kgs.homeslot.module.property.controller;

import com.kgs.homeslot.common.response.ApiResponse;
import com.kgs.homeslot.module.property.dto.*;
import com.kgs.homeslot.module.property.service.BuilderService;
import com.kgs.homeslot.module.user.dto.BuilderProfileDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/builder")
@PreAuthorize("hasAnyAuthority('ROLE_BUILDER', 'ROLE_ADMIN')")
public class BuilderController {

    private final BuilderService builderService;

    public BuilderController(BuilderService builderService) {
        this.builderService = builderService;
    }

    // --- DASHBOARD METRICS ---

    @GetMapping("/dashboard/metrics")
    public ResponseEntity<ApiResponse<BuilderDashboardMetricsDto>> getDashboardMetrics(Authentication auth) {
        BuilderDashboardMetricsDto metrics = builderService.getDashboardMetrics(auth.getName());
        return ResponseEntity.ok(ApiResponse.success("Builder metrics fetched successfully", metrics));
    }

    // --- PROPERTY MANAGEMENT ---

    @GetMapping("/properties")
    public ResponseEntity<ApiResponse<List<PropertyDto>>> getBuilderProperties(Authentication auth) {
        List<PropertyDto> list = builderService.getBuilderProperties(auth.getName());
        return ResponseEntity.ok(ApiResponse.success("Builder properties fetched successfully", list));
    }

    @PostMapping("/properties")
    public ResponseEntity<ApiResponse<PropertyDto>> createProperty(
            @Valid @RequestBody CreatePropertyRequest request,
            Authentication auth) {
        PropertyDto created = builderService.createProperty(request, auth.getName());
        return ResponseEntity.ok(ApiResponse.success("Property created successfully", created));
    }

    @PutMapping("/properties/{id}")
    public ResponseEntity<ApiResponse<PropertyDto>> updateProperty(
            @PathVariable Long id,
            @RequestBody UpdatePropertyRequest request,
            Authentication auth) {
        PropertyDto updated = builderService.updateProperty(id, request, auth.getName());
        return ResponseEntity.ok(ApiResponse.success("Property updated successfully", updated));
    }

    @DeleteMapping("/properties/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProperty(@PathVariable Long id, Authentication auth) {
        builderService.deleteProperty(id, auth.getName());
        return ResponseEntity.ok(ApiResponse.success("Property deleted successfully"));
    }

    // --- INQUIRIES & LEAD MANAGEMENT ---

    @GetMapping("/inquiries")
    public ResponseEntity<ApiResponse<List<PropertyInquiryDto>>> getBuilderInquiries(Authentication auth) {
        List<PropertyInquiryDto> list = builderService.getBuilderInquiries(auth.getName());
        return ResponseEntity.ok(ApiResponse.success("Builder inquiries fetched successfully", list));
    }

    @PostMapping("/inquiries/{id}/reply")
    public ResponseEntity<ApiResponse<PropertyInquiryDto>> replyInquiry(
            @PathVariable Long id,
            @Valid @RequestBody ReplyInquiryRequest request,
            Authentication auth) {
        PropertyInquiryDto inquiry = builderService.replyInquiry(id, request, auth.getName());
        return ResponseEntity.ok(ApiResponse.success("Reply sent successfully", inquiry));
    }

    // --- SITE VISITS MANAGEMENT ---

    @GetMapping("/site-visits")
    public ResponseEntity<ApiResponse<List<SiteVisitDto>>> getBuilderSiteVisits(Authentication auth) {
        List<SiteVisitDto> list = builderService.getBuilderSiteVisits(auth.getName());
        return ResponseEntity.ok(ApiResponse.success("Builder site visits fetched successfully", list));
    }

    @PatchMapping("/site-visits/{id}/status")
    public ResponseEntity<ApiResponse<SiteVisitDto>> updateSiteVisitStatus(
            @PathVariable Long id,
            @RequestParam String status,
            Authentication auth) {
        SiteVisitDto updated = builderService.updateSiteVisitStatus(id, status, auth.getName());
        return ResponseEntity.ok(ApiResponse.success("Site visit status updated to " + status, updated));
    }

    // --- PROFILE ---

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<BuilderProfileDto>> getBuilderProfile(Authentication auth) {
        BuilderProfileDto profile = builderService.getBuilderProfile(auth.getName());
        return ResponseEntity.ok(ApiResponse.success("Builder profile fetched successfully", profile));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<BuilderProfileDto>> updateBuilderProfile(
            @RequestBody BuilderProfileDto dto,
            Authentication auth) {
        BuilderProfileDto updated = builderService.updateBuilderProfile(dto, auth.getName());
        return ResponseEntity.ok(ApiResponse.success("Builder profile updated successfully", updated));
    }
}
