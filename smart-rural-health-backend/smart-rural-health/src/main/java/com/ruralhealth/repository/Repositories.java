package com.ruralhealth.repository;

import com.ruralhealth.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// ── User Repository ──────────────────────────────────────────────
@Repository
interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByMobile(String mobile);
    Optional<User> findByEmail(String email);
    boolean existsByMobile(String mobile);
    boolean existsByEmail(String email);
    List<User> findByRole(User.Role role);
}

// ── Doctor Repository ────────────────────────────────────────────
@Repository
interface DoctorRepository extends JpaRepository<Doctor, Long> {

    List<Doctor> findByVerifiedTrue();
    List<Doctor> findBySpecializationContainingIgnoreCaseAndVerifiedTrue(String specialization);
    List<Doctor> findByAvailableNowTrueAndVerifiedTrue();
    Optional<Doctor> findByUserId(Long userId);

    // Find doctors within a radius using Haversine formula
    @Query(value = """
        SELECT d.* FROM doctors d
        WHERE d.verified = true
          AND (
            6371 * acos(
              cos(radians(:lat)) * cos(radians(d.latitude))
              * cos(radians(d.longitude) - radians(:lng))
              + sin(radians(:lat)) * sin(radians(d.latitude))
            )
          ) < :radiusKm
        ORDER BY (
            6371 * acos(
              cos(radians(:lat)) * cos(radians(d.latitude))
              * cos(radians(d.longitude) - radians(:lng))
              + sin(radians(:lat)) * sin(radians(d.latitude))
            )
          ) ASC
        """, nativeQuery = true)
    List<Doctor> findNearbyDoctors(@Param("lat") double lat,
                                   @Param("lng") double lng,
                                   @Param("radiusKm") double radiusKm);
}

// ── Appointment Repository ───────────────────────────────────────
@Repository
interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientIdOrderByAppointmentDateDesc(Long patientId);
    List<Appointment> findByDoctorIdOrderByAppointmentDateAsc(Long doctorId);
    List<Appointment> findByDoctorIdAndStatus(Long doctorId, Appointment.AppointmentStatus status);
}

// ── Prescription Repository ──────────────────────────────────────
@Repository
interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    List<Prescription> findByPatientIdOrderByCreatedAtDesc(Long patientId);
    List<Prescription> findByDoctorIdOrderByCreatedAtDesc(Long doctorId);
    Optional<Prescription> findByRxNumber(String rxNumber);
}

// ── Feedback Repository ──────────────────────────────────────────
@Repository
interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByDoctorId(Long doctorId);
    boolean existsByPatientIdAndAppointmentId(Long patientId, Long appointmentId);
}
