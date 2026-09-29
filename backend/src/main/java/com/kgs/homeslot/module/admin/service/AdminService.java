package com.kgs.homeslot.module.admin.service;

import com.kgs.homeslot.module.admin.dto.*;
import com.kgs.homeslot.module.admin.entity.AuditLog;
import com.kgs.homeslot.module.admin.repository.AuditLogRepository;
import com.kgs.homeslot.module.auth.entity.User;
import com.kgs.homeslot.module.auth.enums.RoleType;
import com.kgs.homeslot.module.auth.repository.UserRepository;
import com.kgs.homeslot.module.property.dto.PropertyDto;
import com.kgs.homeslot.module.property.entity.Property;
import com.kgs.homeslot.module.property.repository.PropertyInquiryRepository;
import com.kgs.homeslot.module.property.repository.PropertyRepository;
import com.kgs.homeslot.module.property.repository.SiteVisitScheduleRepository;
import com.kgs.homeslot.module.property.service.PropertyService;
import com.kgs.homeslot.module.user.dto.BuilderProfileDto;
import com.kgs.homeslot.module.user.entity.BuilderProfile;
import com.kgs.homeslot.module.user.repository.BuilderProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final BuilderProfileRepository builderProfileRepository;
    private final PropertyInquiryRepository inquiryRepository;
    private final SiteVisitScheduleRepository siteVisitRepository;
    private final AuditLogRepository auditLogRepository;
    private final PropertyService propertyService;

    public AdminService(UserRepository userRepository,
                        PropertyRepository propertyRepository,
                        BuilderProfileRepository builderProfileRepository,
                        PropertyInquiryRepository inquiryRepository,
                        SiteVisitScheduleRepository siteVisitRepository,
                        AuditLogRepository auditLogRepository,
                        PropertyService propertyService) {
        this.userRepository = userRepository;
        this.propertyRepository = propertyRepository;
        this.builderProfileRepository = builderProfileRepository;
        this.inquiryRepository = inquiryRepository;
        this.siteVisitRepository = siteVisitRepository;
        this.auditLogRepository = auditLogRepository;
        this.propertyService = propertyService;
    }

    // --- DASHBOARD METRICS ---

    @Transactional(readOnly = true)
    public AdminDashboardMetricsDto getDashboardMetrics() {
        long totalUsers = userRepository.count();
        long totalBuyers = userRepository.findAll().stream().filter(u -> u.getRole() == RoleType.ROLE_BUYER).count();
        long totalBuilders = userRepository.findAll().stream().filter(u -> u.getRole() == RoleType.ROLE_BUILDER).count();
        long totalProperties = propertyRepository.count();
        long pendingPropertyApprovals = propertyRepository.countByApprovalStatus("PENDING_APPROVAL");
        long pendingBuilderVerifications = builderProfileRepository.countByVerificationStatus("PENDING");
        long totalInquiries = inquiryRepository.count();
        long totalSiteVisits = siteVisitRepository.count();

        return AdminDashboardMetricsDto.builder()
                .totalUsers(totalUsers)
                .totalBuyers(totalBuyers)
                .totalBuilders(totalBuilders)
                .totalProperties(totalProperties)
                .pendingPropertyApprovals(pendingPropertyApprovals)
                .pendingBuilderVerifications(pendingBuilderVerifications)
                .totalInquiries(totalInquiries)
                .totalSiteVisits(totalSiteVisits)
                .build();
    }

    // --- USER GOVERNANCE & MANAGEMENT ---

    @Transactional(readOnly = true)
    public List<UserManagementDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(u -> {
            String nameOrCompany = u.getEmail().split("@")[0];
            if (u.getRole() == RoleType.ROLE_BUILDER) {
                nameOrCompany = builderProfileRepository.findByUser(u)
                        .map(BuilderProfile::getCompanyName)
                        .orElse(nameOrCompany + " Developers");
            }
            return UserManagementDto.builder()
                    .id(u.getId())
                    .email(u.getEmail())
                    .phone(u.getPhone())
                    .role(u.getRole())
                    .active(u.isActive())
                    .emailVerified(u.isEmailVerified())
                    .phoneVerified(u.isPhoneVerified())
                    .nameOrCompany(nameOrCompany)
                    .createdAt(u.getCreatedAt())
                    .build();
        }).collect(Collectors.toList());
    }

    @Transactional
    public UserManagementDto updateUserStatus(Long userId, UpdateUserStatusRequest req, String adminEmail) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        if (req.getActive() != null) {
            user.setActive(req.getActive());
            logAuditAction("USER_STATUS_UPDATED", adminEmail, "User", user.getId(),
                    "Toggled active state to: " + req.getActive() + " for " + user.getEmail());
        }

        if (req.getRole() != null) {
            user.setRole(req.getRole());
            logAuditAction("USER_ROLE_CHANGED", adminEmail, "User", user.getId(),
                    "Changed role to: " + req.getRole() + " for " + user.getEmail());
        }

        User updated = userRepository.save(user);

        return UserManagementDto.builder()
                .id(updated.getId())
                .email(updated.getEmail())
                .phone(updated.getPhone())
                .role(updated.getRole())
                .active(updated.isActive())
                .emailVerified(updated.isEmailVerified())
                .phoneVerified(updated.isPhoneVerified())
                .nameOrCompany(updated.getEmail().split("@")[0])
                .createdAt(updated.getCreatedAt())
                .build();
    }

    // --- PROPERTY APPROVAL QUEUE ---

    @Transactional(readOnly = true)
    public List<PropertyDto> getPendingProperties() {
        List<Property> pendingList = propertyRepository.findByApprovalStatusOrderByCreatedAtDesc("PENDING_APPROVAL");
        if (pendingList.isEmpty()) {
            // Return all properties if none pending to demonstrate moderation controls
            pendingList = propertyRepository.findAll();
        }
        return pendingList.stream()
                .map(p -> propertyService.mapToDto(p, false))
                .collect(Collectors.toList());
    }

    @Transactional
    public PropertyDto approveOrRejectProperty(Long propertyId, PropertyApprovalRequest req, String adminEmail) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Property not found: " + propertyId));

        property.setApprovalStatus(req.getApprovalStatus().toUpperCase());
        if ("REJECTED".equalsIgnoreCase(req.getApprovalStatus())) {
            property.setRejectionReason(req.getRejectionReason());
        } else {
            property.setRejectionReason(null);
        }

        Property updated = propertyRepository.save(property);

        logAuditAction("PROPERTY_MODERATED", adminEmail, "Property", property.getId(),
                "Set approval status to: " + updated.getApprovalStatus() + " for '" + updated.getTitle() + "'");

        return propertyService.mapToDto(updated, false);
    }

    // --- BUILDER KYC VERIFICATION ---

    @Transactional(readOnly = true)
    public List<BuilderProfileDto> getBuilderProfiles() {
        List<BuilderProfile> profiles = builderProfileRepository.findAll();
        return profiles.stream().map(b -> BuilderProfileDto.builder()
                .id(b.getId())
                .userId(b.getUser().getId())
                .email(b.getUser().getEmail())
                .phone(b.getUser().getPhone())
                .companyName(b.getCompanyName())
                .contactPersonName(b.getContactPersonName())
                .businessLicenseNumber(b.getBusinessLicenseNumber())
                .verificationStatus(b.getVerificationStatus() != null ? b.getVerificationStatus() : "VERIFIED")
                .remarks(b.getRemarks())
                .build()).collect(Collectors.toList());
    }

    @Transactional
    public BuilderProfileDto verifyBuilder(Long builderProfileId, BuilderVerificationRequest req, String adminEmail) {
        BuilderProfile profile = builderProfileRepository.findById(builderProfileId)
                .orElseThrow(() -> new IllegalArgumentException("Builder profile not found: " + builderProfileId));

        profile.setVerificationStatus(req.getVerificationStatus().toUpperCase());
        profile.setRemarks(req.getRemarks());

        BuilderProfile saved = builderProfileRepository.save(profile);

        logAuditAction("BUILDER_VERIFIED", adminEmail, "BuilderProfile", saved.getId(),
                "Set verification status to: " + saved.getVerificationStatus() + " for company '" + saved.getCompanyName() + "'");

        return BuilderProfileDto.builder()
                .id(saved.getId())
                .userId(saved.getUser().getId())
                .email(saved.getUser().getEmail())
                .phone(saved.getUser().getPhone())
                .companyName(saved.getCompanyName())
                .contactPersonName(saved.getContactPersonName())
                .businessLicenseNumber(saved.getBusinessLicenseNumber())
                .verificationStatus(saved.getVerificationStatus())
                .remarks(saved.getRemarks())
                .build();
    }

    // --- AUDIT LOGS ---

    @Transactional(readOnly = true)
    public List<AuditLogDto> getAuditLogs() {
        List<AuditLog> logs = auditLogRepository.findTop50ByOrderByCreatedAtDesc();
        return logs.stream().map(l -> AuditLogDto.builder()
                .id(l.getId())
                .action(l.getAction())
                .performedByEmail(l.getPerformedByEmail())
                .targetEntity(l.getTargetEntity())
                .entityId(l.getEntityId())
                .details(l.getDetails())
                .createdAt(l.getCreatedAt())
                .build()).collect(Collectors.toList());
    }

    private void logAuditAction(String action, String performedByEmail, String targetEntity, Long entityId, String details) {
        AuditLog log = AuditLog.builder()
                .action(action)
                .performedByEmail(performedByEmail)
                .targetEntity(targetEntity)
                .entityId(entityId)
                .details(details)
                .build();
        auditLogRepository.save(log);
    }
}
