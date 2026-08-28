package com.kgs.homeslot.module.property.controller;

import com.kgs.homeslot.common.response.ApiResponse;
import com.kgs.homeslot.module.property.dto.*;
import com.kgs.homeslot.module.property.service.PropertyService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/properties")
public class PropertyController {

    private final PropertyService propertyService;

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PropertyDto>>> searchProperties(
            @ModelAttribute PropertySearchRequest searchRequest,
            Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        Page<PropertyDto> result = propertyService.searchProperties(searchRequest, email);
        return ResponseEntity.ok(ApiResponse.success("Properties fetched successfully", result));
    }

    @GetMapping("/featured")
    public ResponseEntity<ApiResponse<List<PropertyDto>>> getFeaturedProperties(Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        List<PropertyDto> list = propertyService.getFeaturedProperties(email);
        return ResponseEntity.ok(ApiResponse.success("Featured properties fetched", list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PropertyDto>> getPropertyById(@PathVariable Long id, Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        PropertyDto dto = propertyService.getPropertyById(id, email);
        return ResponseEntity.ok(ApiResponse.success("Property details fetched", dto));
    }

    @PostMapping("/{id}/reviews")
    public ResponseEntity<ApiResponse<PropertyReviewDto>> addReview(
            @PathVariable Long id,
            @Valid @RequestBody CreateReviewRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        PropertyReviewDto review = propertyService.addReview(id, request, email);
        return ResponseEntity.ok(ApiResponse.success("Review submitted successfully", review));
    }

    @GetMapping("/{id}/reviews")
    public ResponseEntity<ApiResponse<List<PropertyReviewDto>>> getPropertyReviews(@PathVariable Long id) {
        List<PropertyReviewDto> reviews = propertyService.getPropertyReviews(id);
        return ResponseEntity.ok(ApiResponse.success("Property reviews fetched", reviews));
    }

    @GetMapping("/{id}/brochure")
    public ResponseEntity<byte[]> downloadBrochure(@PathVariable Long id) {
        // Generate simulated dynamic property brochure document
        PropertyDto p = propertyService.getPropertyById(id, null);
        String htmlContent = "<html><head><title>" + p.getTitle() + " - Brochure</title></head>"
                + "<body style='font-family: sans-serif; padding: 40px; color: #1e293b;'>"
                + "<h1 style='color: #0f172a; border-bottom: 2px solid #2563eb; padding-bottom: 10px;'>" + p.getTitle() + "</h1>"
                + "<h3 style='color: #2563eb;'>Price: ₹" + p.getPrice() + " | Type: " + p.getPropertyType() + " | BHK: " + p.getBhk() + "</h3>"
                + "<p><strong>Address:</strong> " + p.getAddress() + ", " + p.getCity() + ", " + p.getState() + "</p>"
                + "<p><strong>Area:</strong> " + p.getAreaSqft() + " Sq.Ft. | <strong>Status:</strong> " + p.getStatus() + "</p>"
                + "<hr/>"
                + "<h3>Description</h3>"
                + "<p>" + p.getDescription() + "</p>"
                + "<h3>Amenities</h3>"
                + "<p>" + p.getAmenities() + "</p>"
                + "<hr/>"
                + "<footer style='margin-top: 30px; font-size: 12px; color: #64748b;'>HomeSlot Real Estate Platform - Contact Builder: " + p.getBuilderName() + "</footer>"
                + "</body></html>";

        byte[] body = htmlContent.getBytes();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=property-brochure-" + id + ".html")
                .contentType(MediaType.TEXT_HTML)
                .body(body);
    }
}
