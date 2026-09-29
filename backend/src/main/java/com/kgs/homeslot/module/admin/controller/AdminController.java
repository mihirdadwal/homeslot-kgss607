package com.kgs.homeslot.module.admin.controller;

import com.kgs.homeslot.common.response.ApiResponse;
import com.kgs.homeslot.module.admin.dto.*;
import com.kgs.homeslot.module.admin.service.AdminService;
import com.kgs.homeslot.module.property.dto.PropertyDto;
import com.kgs.homeslot.module.user.dto.BuilderProfileDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // --- METRICS ---

    @GetMapping("/dashboard/metrics")
    public ResponseEntity<ApiResponse<AdminDashboardMetricsDto>> getDashboardMetrics() {
        AdminDashboardMetricsDto metrics = adminService.getDashboardMetrics();
        return ResponseEntity.ok(ApiResponse.success("Admin dashboard metrics fetched", metrics));
    }

    // --- USER GOVERNANCE ---

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserManagementDto>>> getAllUsers() {
        List<UserManagementDto> users = adminService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success("Users fetched successfully", users));
    }

    @PutMapping("/users/{id}/status")
    public ResponseEntity<ApiResponse<UserManagementDto>> updateUserStatus(
            @PathVariable Long id,
            @RequestBody UpdateUserStatusRequest request,
            Authentication auth) {
        UserManagementDto updated = adminService.updateUserStatus(id, request, auth.getName());
        return ResponseEntity.ok(ApiResponse.success("User status updated successfully", updated));
    }

    // --- PROPERTY MODERATION & APPROVAL QUEUE ---

    @GetMapping("/properties/pending")
    public ResponseEntity<ApiResponse<List<PropertyDto>>> getPendingProperties() {
        List<PropertyDto> list = adminService.getPendingProperties();
        return ResponseEntity.ok(ApiResponse.success("Pending property listings fetched", list));
    }

    @PostMapping("/properties/{id}/approval")
    public ResponseEntity<ApiResponse<PropertyDto>> approveOrRejectProperty(
            @PathVariable Long id,
            @Valid @RequestBody PropertyApprovalRequest request,
            Authentication auth) {
        PropertyDto updated = adminService.approveOrRejectProperty(id, request, auth.getName());
        return ResponseEntity.ok(ApiResponse.success("Property approval status updated to " + updated.getApprovalStatus(), updated));
    }

    // --- BUILDER KYC VERIFICATION ---

    @GetMapping("/builders")
    public ResponseEntity<ApiResponse<List<BuilderProfileDto>>> getBuilderProfiles() {
        List<BuilderProfileDto> list = adminService.getBuilderProfiles();
        return ResponseEntity.ok(ApiResponse.success("Builder profiles fetched successfully", list));
    }

    @PostMapping("/builders/{id}/verification")
    public ResponseEntity<ApiResponse<BuilderProfileDto>> verifyBuilder(
            @PathVariable Long id,
            @Valid @RequestBody BuilderVerificationRequest request,
            Authentication auth) {
        BuilderProfileDto updated = adminService.verifyBuilder(id, request, auth.getName());
        return ResponseEntity.ok(ApiResponse.success("Builder verification status updated to " + updated.getVerificationStatus(), updated));
    }

    // --- AUDIT LOGS ---

    @GetMapping("/audit-logs")
    public ResponseEntity<ApiResponse<List<AuditLogDto>>> getAuditLogs() {
        List<AuditLogDto> logs = adminService.getAuditLogs();
        return ResponseEntity.ok(ApiResponse.success("Audit logs fetched successfully", logs));
    }
}
