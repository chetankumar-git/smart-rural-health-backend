package com.ruralhealth.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "prescriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    private String diagnosis;

    @Column(columnDefinition = "TEXT")
    private String medicines;   // JSON string: [{name, dose, timing, days}]

    @Column(columnDefinition = "TEXT")
    private String doctorNotes;

    private LocalDate prescriptionDate;
    private String rxNumber;            // Unique prescription number

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (prescriptionDate == null) prescriptionDate = LocalDate.now();
        if (rxNumber == null) rxNumber = "RX" + System.currentTimeMillis();
    }
}
