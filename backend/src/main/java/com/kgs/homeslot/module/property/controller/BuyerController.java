package com.kgs.homeslot.module.property.controller;

import com.kgs.homeslot.common.response.ApiResponse;
import com.kgs.homeslot.module.property.dto.*;
import com.kgs.homeslot.module.property.service.BuyerService;
import com.kgs.homeslot.module.user.dto.BuyerProfileDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/buyer")
@PreAuthorize("hasAuthority('ROLE_BUYER')")
public class BuyerController {

    private final BuyerService buyerService;

    public BuyerController(BuyerService buyerService) {
        this.buyerService = buyerService;
    }

    // --- FAVORITES ---

    @PostMapping("/favorites/{propertyId}")
    public ResponseEntity<ApiResponse<Void>> addFavorite(@PathVariable Long propertyId, Authentication auth) {
        buyerService.addFavorite(propertyId, auth.getName());
        return ResponseEntity.ok(ApiResponse.success("Property added to favorites"));
    }

    @DeleteMapping("/favorites/{propertyId}")
    public ResponseEntity<ApiResponse<Void>> removeFavorite(@PathVariable Long propertyId, Authentication auth) {
        buyerService.removeFavorite(propertyId, auth.getName());
        return ResponseEntity.ok(ApiResponse.success("Property removed from favorites"));
    }

    @GetMapping("/favorites")
    public ResponseEntity<ApiResponse<List<PropertyDto>>> getFavoriteProperties(Authentication auth) {
        List<PropertyDto> list = buyerService.getFavoriteProperties(auth.getName());
        return ResponseEntity.ok(ApiResponse.success("Favorite properties fetched", list));
    }

    // --- RECENTLY VIEWED ---

    @GetMapping("/recently-viewed")
    public ResponseEntity<ApiResponse<List<PropertyDto>>> getRecentlyViewed(Authentication auth) {
        List<PropertyDto> list = buyerService.getRecentlyViewed(auth.getName());
        return ResponseEntity.ok(ApiResponse.success("Recently viewed properties fetched", list));
    }

    @DeleteMapping("/recently-viewed")
    public ResponseEntity<ApiResponse<Void>> clearRecentlyViewed(Authentication auth) {
        buyerService.clearRecentlyViewed(auth.getName());
        return ResponseEntity.ok(ApiResponse.success("Recently viewed history cleared"));
    }

    // --- INQUIRIES ---

    @PostMapping("/inquiries")
    public ResponseEntity<ApiResponse<PropertyInquiryDto>> createInquiry(
            @Valid @RequestBody CreateInquiryRequest request,
            Authentication auth) {
        PropertyInquiryDto inquiry = buyerService.createInquiry(request, auth.getName());
        return ResponseEntity.ok(ApiResponse.success("Inquiry sent to builder successfully", inquiry));
    }

    @GetMapping("/inquiries")
    public ResponseEntity<ApiResponse<List<PropertyInquiryDto>>> getBuyerInquiries(Authentication auth) {
        List<PropertyInquiryDto> list = buyerService.getBuyerInquiries(auth.getName());
        return ResponseEntity.ok(ApiResponse.success("Inquiries fetched successfully", list));
    }

    // --- SITE VISITS ---

    @PostMapping("/site-visits")
    public ResponseEntity<ApiResponse<SiteVisitDto>> scheduleSiteVisit(
            @Valid @RequestBody CreateSiteVisitRequest request,
            Authentication auth) {
        SiteVisitDto visit = buyerService.scheduleSiteVisit(request, auth.getName());
        return ResponseEntity.ok(ApiResponse.success("Site visit scheduled successfully", visit));
    }

    @GetMapping("/site-visits")
    public ResponseEntity<ApiResponse<List<SiteVisitDto>>> getBuyerSiteVisits(Authentication auth) {
        List<SiteVisitDto> list = buyerService.getBuyerSiteVisits(auth.getName());
        return ResponseEntity.ok(ApiResponse.success("Site visits fetched successfully", list));
    }

    @PatchMapping("/site-visits/{id}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelSiteVisit(@PathVariable Long id, Authentication auth) {
        buyerService.cancelSiteVisit(id, auth.getName());
        return ResponseEntity.ok(ApiResponse.success("Site visit cancelled"));
    }

    // --- METRICS ---

    @GetMapping("/dashboard/metrics")
    public ResponseEntity<ApiResponse<BuyerDashboardMetricsDto>> getDashboardMetrics(Authentication auth) {
        BuyerDashboardMetricsDto metrics = buyerService.getDashboardMetrics(auth.getName());
        return ResponseEntity.ok(ApiResponse.success("Buyer metrics fetched", metrics));
    }

    // --- PROFILE ---

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<BuyerProfileDto>> getProfile(Authentication auth) {
        BuyerProfileDto profile = buyerService.getProfile(auth.getName());
        return ResponseEntity.ok(ApiResponse.success("Buyer profile fetched", profile));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<BuyerProfileDto>> updateProfile(
            @RequestBody BuyerProfileDto profileDto,
            Authentication auth) {
        BuyerProfileDto updated = buyerService.updateProfile(profileDto, auth.getName());
        return ResponseEntity.ok(ApiResponse.success("Buyer profile updated successfully", updated));
    }
}
