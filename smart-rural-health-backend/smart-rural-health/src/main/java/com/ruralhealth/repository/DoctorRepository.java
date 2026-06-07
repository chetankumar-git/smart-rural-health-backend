package com.ruralhealth.repository;

import com.ruralhealth.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    List<Doctor> findByVerifiedTrue();

    List<Doctor> findBySpecializationContainingIgnoreCaseAndVerifiedTrue(String specialization);

    List<Doctor> findByAvailableNowTrueAndVerifiedTrue();

    Optional<Doctor> findByUserId(Long userId);

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
    List<Doctor> findNearbyDoctors(
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("radiusKm") double radiusKm);
}
