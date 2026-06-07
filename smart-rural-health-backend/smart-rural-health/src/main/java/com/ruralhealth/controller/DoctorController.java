package com.ruralhealth.controller;

import com.ruralhealth.model.Doctor;
import com.ruralhealth.repository.*;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    @Autowired private DoctorRepository doctorRepository;

    // ── Public: Nearby doctors (no login needed) ──────────────────
    @GetMapping("/nearby")
    public ResponseEntity<?> getNearbyDoctors(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "10") double radiusKm) {
        List<Doctor> doctors = doctorRepository.findNearbyDoctors(lat, lng, radiusKm);
        return ResponseEntity.ok(doctors);
    }

    // ── Public: Search by specialization ─────────────────────────
    @GetMapping("/search")
    public ResponseEntity<?> searchBySpecialization(@RequestParam String specialization) {
        List<Doctor> doctors = doctorRepository
                .findBySpecializationContainingIgnoreCaseAndVerifiedTrue(specialization);
        return ResponseEntity.ok(doctors);
    }

    // ── Public: Available now ─────────────────────────────────────
    @GetMapping("/available")
    public ResponseEntity<?> getAvailableDoctors() {
        return ResponseEntity.ok(doctorRepository.findByAvailableNowTrueAndVerifiedTrue());
    }

    // ── Doctor: Update own availability ──────────────────────────
    @PreAuthorize("hasRole('DOCTOR')")
    @PutMapping("/{id}/availability")
    public ResponseEntity<?> updateAvailability(@PathVariable Long id,
                                                 @RequestBody Map<String, Boolean> body) {
        return doctorRepository.findById(id).map(doc -> {
            doc.setAvailableNow(body.getOrDefault("availableNow", false));
            doctorRepository.save(doc);
            return ResponseEntity.ok(Map.of("message", "Availability updated"));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ── Admin: Verify doctor ──────────────────────────────────────
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/verify")
    public ResponseEntity<?> verifyDoctor(@PathVariable Long id) {
        return doctorRepository.findById(id).map(doc -> {
            doc.setVerified(true);
            doctorRepository.save(doc);
            return ResponseEntity.ok(Map.of("message", "Doctor verified successfully"));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ── Admin: Get all doctors ────────────────────────────────────
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<?> getAllDoctors() {
        return ResponseEntity.ok(doctorRepository.findAll());
    }
}
