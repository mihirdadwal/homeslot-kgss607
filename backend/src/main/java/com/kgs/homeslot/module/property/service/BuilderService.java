package com.kgs.homeslot.module.property.service;

import com.kgs.homeslot.module.auth.entity.User;
import com.kgs.homeslot.module.auth.repository.UserRepository;
import com.kgs.homeslot.module.property.dto.*;
import com.kgs.homeslot.module.property.entity.Property;
import com.kgs.homeslot.module.property.entity.PropertyInquiry;
import com.kgs.homeslot.module.property.entity.SiteVisitSchedule;
import com.kgs.homeslot.module.property.repository.PropertyInquiryRepository;
import com.kgs.homeslot.module.property.repository.PropertyRepository;
import com.kgs.homeslot.module.property.repository.SiteVisitScheduleRepository;
import com.kgs.homeslot.module.user.dto.BuilderProfileDto;
import com.kgs.homeslot.module.user.entity.BuilderProfile;
import com.kgs.homeslot.module.user.repository.BuilderProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BuilderService {

    private final PropertyRepository propertyRepository;
    private final PropertyInquiryRepository inquiryRepository;
    private final SiteVisitScheduleRepository siteVisitRepository;
    private final UserRepository userRepository;
    private final BuilderProfileRepository builderProfileRepository;
    private final PropertyService propertyService;

    public BuilderService(PropertyRepository propertyRepository,
                          PropertyInquiryRepository inquiryRepository,
                          SiteVisitScheduleRepository siteVisitRepository,
                          UserRepository userRepository,
                          BuilderProfileRepository builderProfileRepository,
                          PropertyService propertyService) {
        this.propertyRepository = propertyRepository;
        this.inquiryRepository = inquiryRepository;
        this.siteVisitRepository = siteVisitRepository;
        this.userRepository = userRepository;
        this.builderProfileRepository = builderProfileRepository;
        this.propertyService = propertyService;
    }

    // --- DASHBOARD METRICS ---

    @Transactional(readOnly = true)
    public BuilderDashboardMetricsDto getDashboardMetrics(String currentUserEmail) {
        User user = getUserByEmail(currentUserEmail);
        long totalProperties = propertyRepository.countByBuilderId(user.getId());
        long activeListings = totalProperties; // In current schema, all posted properties are active
        long totalInquiries = inquiryRepository.countByBuilderId(user.getId());
        long pendingInquiries = inquiryRepository.countByBuilderIdAndStatus(user.getId(), "PENDING");
        long totalSiteVisits = siteVisitRepository.countByPropertyBuilderId(user.getId());
        long upcomingSiteVisits = siteVisitRepository.countByPropertyBuilderIdAndStatus(user.getId(), "SCHEDULED");

        return BuilderDashboardMetricsDto.builder()
                .totalProperties(totalProperties)
                .activeListings(activeListings)
                .totalInquiries(totalInquiries)
                .pendingInquiries(pendingInquiries)
                .totalSiteVisits(totalSiteVisits)
                .upcomingSiteVisits(upcomingSiteVisits)
                .build();
    }

    // --- PROPERTY MANAGEMENT ---

    @Transactional(readOnly = true)
    public List<PropertyDto> getBuilderProperties(String currentUserEmail) {
        User user = getUserByEmail(currentUserEmail);
        List<Property> properties = propertyRepository.findByBuilderIdOrderByCreatedAtDesc(user.getId());
        return properties.stream()
                .map(p -> propertyService.mapToDto(p, false))
                .collect(Collectors.toList());
    }

    @Transactional
    public PropertyDto createProperty(CreatePropertyRequest req, String currentUserEmail) {
        User user = getUserByEmail(currentUserEmail);

        // Fetch builder profile for company name if available
        Optional<BuilderProfile> profileOpt = builderProfileRepository.findByUser(user);
        String builderName = profileOpt.map(BuilderProfile::getCompanyName)
                .orElse(user.getEmail().split("@")[0] + " Developers");

        Property property = Property.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .propertyType(req.getPropertyType())
                .listingType(req.getListingType())
                .price(req.getPrice())
                .bhk(req.getBhk())
                .bathrooms(req.getBathrooms())
                .areaSqft(req.getAreaSqft())
                .address(req.getAddress())
                .city(req.getCity())
                .state(req.getState())
                .zipCode(req.getZipCode())
                .latitude(req.getLatitude())
                .longitude(req.getLongitude())
                .status(req.getStatus())
                .amenities(req.getAmenities())
                .coverImageUrl(req.getCoverImageUrl())
                .imageUrls(req.getImageUrls())
                .brochureUrl(req.getBrochureUrl())
                .builderId(user.getId())
                .builderName(builderName)
                .avgRating(0.0)
                .reviewCount(0)
                .build();

        Property saved = propertyRepository.save(property);
        return propertyService.mapToDto(saved, false);
    }

    @Transactional
    public PropertyDto updateProperty(Long id, UpdatePropertyRequest req, String currentUserEmail) {
        User user = getUserByEmail(currentUserEmail);
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Property not found: " + id));

        if (!user.getId().equals(property.getBuilderId())) {
            throw new IllegalArgumentException("Unauthorized to modify this property");
        }

        if (req.getTitle() != null) property.setTitle(req.getTitle());
        if (req.getDescription() != null) property.setDescription(req.getDescription());
        if (req.getPropertyType() != null) property.setPropertyType(req.getPropertyType());
        if (req.getListingType() != null) property.setListingType(req.getListingType());
        if (req.getPrice() != null) property.setPrice(req.getPrice());
        if (req.getBhk() != null) property.setBhk(req.getBhk());
        if (req.getBathrooms() != null) property.setBathrooms(req.getBathrooms());
        if (req.getAreaSqft() != null) property.setAreaSqft(req.getAreaSqft());
        if (req.getAddress() != null) property.setAddress(req.getAddress());
        if (req.getCity() != null) property.setCity(req.getCity());
        if (req.getState() != null) property.setState(req.getState());
        if (req.getZipCode() != null) property.setZipCode(req.getZipCode());
        if (req.getLatitude() != null) property.setLatitude(req.getLatitude());
        if (req.getLongitude() != null) property.setLongitude(req.getLongitude());
        if (req.getStatus() != null) property.setStatus(req.getStatus());
        if (req.getAmenities() != null) property.setAmenities(req.getAmenities());
        if (req.getCoverImageUrl() != null) property.setCoverImageUrl(req.getCoverImageUrl());
        if (req.getImageUrls() != null) property.setImageUrls(req.getImageUrls());
        if (req.getBrochureUrl() != null) property.setBrochureUrl(req.getBrochureUrl());

        Property updated = propertyRepository.save(property);
        return propertyService.mapToDto(updated, false);
    }

    @Transactional
    public void deleteProperty(Long id, String currentUserEmail) {
        User user = getUserByEmail(currentUserEmail);
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Property not found: " + id));

        if (!user.getId().equals(property.getBuilderId())) {
            throw new IllegalArgumentException("Unauthorized to delete this property");
        }

        propertyRepository.delete(property);
    }

    // --- INQUIRIES & LEAD MANAGEMENT ---

    @Transactional(readOnly = true)
    public List<PropertyInquiryDto> getBuilderInquiries(String currentUserEmail) {
        User user = getUserByEmail(currentUserEmail);
        List<PropertyInquiry> inquiries = inquiryRepository.findByBuilderIdOrderByCreatedAtDesc(user.getId());
        return inquiries.stream().map(i -> PropertyInquiryDto.builder()
                .id(i.getId())
                .propertyId(i.getProperty().getId())
                .propertyTitle(i.getProperty().getTitle())
                .builderId(i.getBuilderId())
                .name(i.getName())
                .email(i.getEmail())
                .phone(i.getPhone())
                .subject(i.getSubject())
                .message(i.getMessage())
                .status(i.getStatus())
                .replyMessage(i.getReplyMessage())
                .repliedAt(i.getRepliedAt())
                .createdAt(i.getCreatedAt())
                .build()).collect(Collectors.toList());
    }

    @Transactional
    public PropertyInquiryDto replyInquiry(Long inquiryId, ReplyInquiryRequest req, String currentUserEmail) {
        User user = getUserByEmail(currentUserEmail);
        PropertyInquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("Inquiry not found: " + inquiryId));

        if (!user.getId().equals(inquiry.getBuilderId())) {
            throw new IllegalArgumentException("Unauthorized to reply to this inquiry");
        }

        inquiry.setReplyMessage(req.getReplyMessage());
        inquiry.setRepliedAt(LocalDateTime.now());
        inquiry.setStatus("REPLIED");

        PropertyInquiry saved = inquiryRepository.save(inquiry);

        return PropertyInquiryDto.builder()
                .id(saved.getId())
                .propertyId(saved.getProperty().getId())
                .propertyTitle(saved.getProperty().getTitle())
                .builderId(saved.getBuilderId())
                .name(saved.getName())
                .email(saved.getEmail())
                .phone(saved.getPhone())
                .subject(saved.getSubject())
                .message(saved.getMessage())
                .status(saved.getStatus())
                .replyMessage(saved.getReplyMessage())
                .repliedAt(saved.getRepliedAt())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    // --- SITE VISITS MANAGEMENT ---

    @Transactional(readOnly = true)
    public List<SiteVisitDto> getBuilderSiteVisits(String currentUserEmail) {
        User user = getUserByEmail(currentUserEmail);
        List<SiteVisitSchedule> visits = siteVisitRepository.findByPropertyBuilderIdOrderByVisitDateAsc(user.getId());
        return visits.stream().map(v -> SiteVisitDto.builder()
                .id(v.getId())
                .propertyId(v.getProperty().getId())
                .propertyTitle(v.getProperty().getTitle())
                .propertyAddress(v.getProperty().getAddress() + ", " + v.getProperty().getCity())
                .coverImageUrl(v.getProperty().getCoverImageUrl())
                .visitDate(v.getVisitDate())
                .timeSlot(v.getTimeSlot())
                .status(v.getStatus())
                .notes(v.getNotes())
                .createdAt(v.getCreatedAt())
                .build()).collect(Collectors.toList());
    }

    @Transactional
    public SiteVisitDto updateSiteVisitStatus(Long visitId, String status, String currentUserEmail) {
        User user = getUserByEmail(currentUserEmail);
        SiteVisitSchedule visit = siteVisitRepository.findById(visitId)
                .orElseThrow(() -> new IllegalArgumentException("Site visit not found: " + visitId));

        if (!user.getId().equals(visit.getProperty().getBuilderId())) {
            throw new IllegalArgumentException("Unauthorized to update this site visit");
        }

        visit.setStatus(status.toUpperCase());
        SiteVisitSchedule saved = siteVisitRepository.save(visit);

        return SiteVisitDto.builder()
                .id(saved.getId())
                .propertyId(saved.getProperty().getId())
                .propertyTitle(saved.getProperty().getTitle())
                .propertyAddress(saved.getProperty().getAddress() + ", " + saved.getProperty().getCity())
                .coverImageUrl(saved.getProperty().getCoverImageUrl())
                .visitDate(saved.getVisitDate())
                .timeSlot(saved.getTimeSlot())
                .status(saved.getStatus())
                .notes(saved.getNotes())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    // --- PROFILE ---

    @Transactional(readOnly = true)
    public BuilderProfileDto getBuilderProfile(String currentUserEmail) {
        User user = getUserByEmail(currentUserEmail);
        Optional<BuilderProfile> profileOpt = builderProfileRepository.findByUser(user);
        BuilderProfile profile = profileOpt.orElseGet(() -> BuilderProfile.builder()
                .user(user)
                .companyName(user.getEmail().split("@")[0] + " Real Estate")
                .contactPersonName(user.getEmail().split("@")[0])
                .businessLicenseNumber("LIC-PENDING")
                .build());

        return BuilderProfileDto.builder()
                .id(profile.getId())
                .userId(user.getId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .companyName(profile.getCompanyName())
                .contactPersonName(profile.getContactPersonName())
                .businessLicenseNumber(profile.getBusinessLicenseNumber())
                .build();
    }

    @Transactional
    public BuilderProfileDto updateBuilderProfile(BuilderProfileDto dto, String currentUserEmail) {
        User user = getUserByEmail(currentUserEmail);
        BuilderProfile profile = builderProfileRepository.findByUser(user)
                .orElseGet(() -> BuilderProfile.builder().user(user).build());

        if (dto.getCompanyName() != null && !dto.getCompanyName().trim().isEmpty()) {
            profile.setCompanyName(dto.getCompanyName().trim());
        }
        if (dto.getContactPersonName() != null && !dto.getContactPersonName().trim().isEmpty()) {
            profile.setContactPersonName(dto.getContactPersonName().trim());
        }
        if (dto.getBusinessLicenseNumber() != null) {
            profile.setBusinessLicenseNumber(dto.getBusinessLicenseNumber().trim());
        }

        BuilderProfile saved = builderProfileRepository.save(profile);

        return BuilderProfileDto.builder()
                .id(saved.getId())
                .userId(user.getId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .companyName(saved.getCompanyName())
                .contactPersonName(saved.getContactPersonName())
                .businessLicenseNumber(saved.getBusinessLicenseNumber())
                .build();
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));
    }
}
