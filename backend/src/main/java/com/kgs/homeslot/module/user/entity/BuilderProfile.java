package com.kgs.homeslot.module.user.entity;

import com.kgs.homeslot.module.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "builder_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuilderProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "contact_person_name", nullable = false)
    private String contactPersonName;

    @Column(name = "business_license_number")
    private String businessLicenseNumber;
}
