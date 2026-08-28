package com.kgs.homeslot.module.property.service;

import com.kgs.homeslot.module.auth.entity.User;
import com.kgs.homeslot.module.auth.repository.UserRepository;
import com.kgs.homeslot.module.property.dto.*;
import com.kgs.homeslot.module.property.entity.*;
import com.kgs.homeslot.module.property.repository.*;
import com.kgs.homeslot.module.user.dto.BuyerProfileDto;
import com.kgs.homeslot.module.user.entity.BuyerProfile;
import com.kgs.homeslot.module.user.repository.BuyerProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BuyerService {

    private final FavoritePropertyRepository favoriteRepository;
    private final RecentlyViewedRepository recentlyViewedRepository;
    private final PropertyInquiryRepository inquiryRepository;
    private final SiteVisitScheduleRepository siteVisitRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final BuyerProfileRepository buyerProfileRepository;
    private final PropertyService propertyService;

    public BuyerService(FavoritePropertyRepository favoriteRepository,
                        RecentlyViewedRepository recentlyViewedRepository,
                        PropertyInquiryRepository inquiryRepository,
                        SiteVisitScheduleRepository siteVisitRepository,
                        PropertyRepository propertyRepository,
                        UserRepository userRepository,
                        BuyerProfileRepository buyerProfileRepository,
                        PropertyService propertyService) {
        this.favoriteRepository = favoriteRepository;
        this.recentlyViewedRepository = recentlyViewedRepository;
        this.inquiryRepository = inquiryRepository;
        this.siteVisitRepository = siteVisitRepository;
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
        this.buyerProfileRepository = buyerProfileRepository;
        this.propertyService = propertyService;
    }

    // --- FAVORITES ---

    @Transactional
    public void addFavorite(Long propertyId, String currentUserEmail) {
        User user = getUserByEmail(currentUserEmail);
        Property property = getProperty(propertyId);

        if (!favoriteRepository.existsByUserIdAndPropertyId(user.getId(), propertyId)) {
            FavoriteProperty fav = FavoriteProperty.builder()
                    .user(user)
                    .property(property)
                    .build();
            favoriteRepository.save(fav);
        }
    }

    @Transactional
    public void removeFavorite(Long propertyId, String currentUserEmail) {
        User user = getUserByEmail(currentUserEmail);
        favoriteRepository.deleteByUserIdAndPropertyId(user.getId(), propertyId);
    }

    @Transactional(readOnly = true)
    public List<PropertyDto> getFavoriteProperties(String currentUserEmail) {
        User user = getUserByEmail(currentUserEmail);
        List<FavoriteProperty> favs = favoriteRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        return favs.stream()
                .map(f -> propertyService.mapToDto(f.getProperty(), true))
                .collect(Collectors.toList());
    }

    // --- RECENTLY VIEWED ---

    @Transactional(readOnly = true)
    public List<PropertyDto> getRecentlyViewed(String currentUserEmail) {
        User user = getUserByEmail(currentUserEmail);
        List<RecentlyViewed> list = recentlyViewedRepository.findTop10ByUserIdOrderByViewedAtDesc(user.getId());
        return list.stream()
                .map(rv -> propertyService.mapToDto(rv.getProperty(), favoriteRepository.existsByUserIdAndPropertyId(user.getId(), rv.getProperty().getId())))
                .collect(Collectors.toList());
    }

    @Transactional
    public void clearRecentlyViewed(String currentUserEmail) {
        User user = getUserByEmail(currentUserEmail);
        recentlyViewedRepository.deleteByUserId(user.getId());
    }

    // --- INQUIRIES ---

    @Transactional
    public PropertyInquiryDto createInquiry(CreateInquiryRequest req, String currentUserEmail) {
        User user = getUserByEmail(currentUserEmail);
        Property property = getProperty(req.getPropertyId());

        PropertyInquiry inquiry = PropertyInquiry.builder()
                .user(user)
                .property(property)
                .builderId(property.getBuilderId())
                .name(req.getName())
                .email(req.getEmail())
                .phone(req.getPhone())
                .subject(req.getSubject() != null ? req.getSubject() : "Inquiry regarding " + property.getTitle())
                .message(req.getMessage())
                .status("PENDING")
                .build();

        PropertyInquiry saved = inquiryRepository.save(inquiry);

        return PropertyInquiryDto.builder()
                .id(saved.getId())
                .propertyId(property.getId())
                .propertyTitle(property.getTitle())
                .builderId(saved.getBuilderId())
                .name(saved.getName())
                .email(saved.getEmail())
                .phone(saved.getPhone())
                .subject(saved.getSubject())
                .message(saved.getMessage())
                .status(saved.getStatus())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public List<PropertyInquiryDto> getBuyerInquiries(String currentUserEmail) {
        User user = getUserByEmail(currentUserEmail);
        List<PropertyInquiry> inquiries = inquiryRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
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
                .createdAt(i.getCreatedAt())
                .build()).collect(Collectors.toList());
    }

    // --- SITE VISITS ---

    @Transactional
    public SiteVisitDto scheduleSiteVisit(CreateSiteVisitRequest req, String currentUserEmail) {
        User user = getUserByEmail(currentUserEmail);
        Property property = getProperty(req.getPropertyId());

        SiteVisitSchedule visit = SiteVisitSchedule.builder()
                .user(user)
                .property(property)
                .visitDate(req.getVisitDate())
                .timeSlot(req.getTimeSlot())
                .notes(req.getNotes())
                .status("SCHEDULED")
                .build();

        SiteVisitSchedule saved = siteVisitRepository.save(visit);

        return SiteVisitDto.builder()
                .id(saved.getId())
                .propertyId(property.getId())
                .propertyTitle(property.getTitle())
                .propertyAddress(property.getAddress() + ", " + property.getCity())
                .coverImageUrl(property.getCoverImageUrl())
                .visitDate(saved.getVisitDate())
                .timeSlot(saved.getTimeSlot())
                .status(saved.getStatus())
                .notes(saved.getNotes())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public List<SiteVisitDto> getBuyerSiteVisits(String currentUserEmail) {
        User user = getUserByEmail(currentUserEmail);
        List<SiteVisitSchedule> visits = siteVisitRepository.findByUserIdOrderByVisitDateAsc(user.getId());
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
    public void cancelSiteVisit(Long visitId, String currentUserEmail) {
        User user = getUserByEmail(currentUserEmail);
        SiteVisitSchedule visit = siteVisitRepository.findById(visitId)
                .orElseThrow(() -> new IllegalArgumentException("Site visit not found: " + visitId));
        if (!visit.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Unauthorized to cancel this visit");
        }
        visit.setStatus("CANCELLED");
        siteVisitRepository.save(visit);
    }

    // --- DASHBOARD METRICS ---

    @Transactional(readOnly = true)
    public BuyerDashboardMetricsDto getDashboardMetrics(String currentUserEmail) {
        User user = getUserByEmail(currentUserEmail);
        long favCount = favoriteRepository.countByUserId(user.getId());
        long visitCount = siteVisitRepository.countByUserId(user.getId());
        long inquiryCount = inquiryRepository.countByUserId(user.getId());
        long rvCount = recentlyViewedRepository.findTop10ByUserIdOrderByViewedAtDesc(user.getId()).size();

        return BuyerDashboardMetricsDto.builder()
                .totalFavorites(favCount)
                .totalSiteVisits(visitCount)
                .totalInquiries(inquiryCount)
                .totalRecentlyViewed(rvCount)
                .build();
    }

    // --- PROFILE ---

    @Transactional(readOnly = true)
    public BuyerProfileDto getProfile(String currentUserEmail) {
        User user = getUserByEmail(currentUserEmail);
        Optional<BuyerProfile> profileOpt = buyerProfileRepository.findByUserId(user.getId());
        BuyerProfile profile = profileOpt.orElseGet(() -> BuyerProfile.builder()
                .user(user)
                .fullName(user.getEmail().split("@")[0])
                .city("")
                .build());

        return BuyerProfileDto.builder()
                .id(profile.getId())
                .userId(user.getId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .fullName(profile.getFullName())
                .city(profile.getCity())
                .budgetMin(profile.getBudgetMin())
                .budgetMax(profile.getBudgetMax())
                .preferredPropertyType(profile.getPreferredPropertyType())
                .preferredBhk(profile.getPreferredBhk())
                .build();
    }

    @Transactional
    public BuyerProfileDto updateProfile(BuyerProfileDto dto, String currentUserEmail) {
        User user = getUserByEmail(currentUserEmail);
        BuyerProfile profile = buyerProfileRepository.findByUserId(user.getId())
                .orElseGet(() -> BuyerProfile.builder().user(user).build());

        if (dto.getFullName() != null && !dto.getFullName().trim().isEmpty()) {
            profile.setFullName(dto.getFullName().trim());
        }
        if (dto.getCity() != null) {
            profile.setCity(dto.getCity().trim());
        }
        profile.setBudgetMin(dto.getBudgetMin());
        profile.setBudgetMax(dto.getBudgetMax());
        profile.setPreferredPropertyType(dto.getPreferredPropertyType());
        profile.setPreferredBhk(dto.getPreferredBhk());

        BuyerProfile saved = buyerProfileRepository.save(profile);

        return BuyerProfileDto.builder()
                .id(saved.getId())
                .userId(user.getId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .fullName(saved.getFullName())
                .city(saved.getCity())
                .budgetMin(saved.getBudgetMin())
                .budgetMax(saved.getBudgetMax())
                .preferredPropertyType(saved.getPreferredPropertyType())
                .preferredBhk(saved.getPreferredBhk())
                .build();
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));
    }

    private Property getProperty(Long propertyId) {
        return propertyRepository.findById(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Property not found: " + propertyId));
    }
}
