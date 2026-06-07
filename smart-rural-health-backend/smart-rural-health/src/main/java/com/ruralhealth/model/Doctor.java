package com.ruralhealth.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "doctors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String specialization;
    private String qualification;
    private Integer experienceYears;
    private String licenseNumber;       // MCI/NMC registration number
    private String hospitalName;
    private String hospitalAddress;
    private Double latitude;
    private Double longitude;
    private String districtName;

    private Double consultationFee;
    private boolean availableNow = false;
    private boolean verified = false;   // Admin must verify

    @Enumerated(EnumType.STRING)
    private ConsultationType consultationType;

    private String availableTimings;    // e.g. "9AM-1PM, 3PM-6PM"
    private Double rating;
    private Integer totalRatings;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum ConsultationType {
        IN_PERSON, VIDEO, BOTH
    }
}
