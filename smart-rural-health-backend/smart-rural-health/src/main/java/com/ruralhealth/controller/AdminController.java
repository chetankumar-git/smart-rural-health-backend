package com.ruralhealth.controller;

import com.ruralhealth.model.User;
import com.ruralhealth.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired private UserRepository userRepository;
    @Autowired private DoctorRepository doctorRepository;
    @Autowired private AppointmentRepository appointmentRepository;
    @Autowired private PrescriptionRepository prescriptionRepository;

    // Dashboard stats
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard() {
        long totalPatients  = userRepository.findByRole(User.Role.PATIENT).size();
        long totalDoctors   = doctorRepository.findByVerifiedTrue().size();
        long totalAppts     = appointmentRepository.count();
        long pendingVerify  = doctorRepository.findAll().stream()
                                .filter(d -> !d.isVerified()).count();

        return ResponseEntity.ok(Map.of(
                "totalPatients",     totalPatients,
                "verifiedDoctors",   totalDoctors,
                "totalAppointments", totalAppts,
                "doctorsPendingVerification", pendingVerify
        ));
    }

    // All users
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    // Disable a user
    @PutMapping("/users/{id}/disable")
    public ResponseEntity<?> disableUser(@PathVariable Long id) {
        return userRepository.findById(id).map(u -> {
            u.setEnabled(false);
            userRepository.save(u);
            return ResponseEntity.ok(Map.of("message", "User disabled"));
        }).orElse(ResponseEntity.notFound().build());
    }

    // All appointments
    @GetMapping("/appointments")
    public ResponseEntity<?> getAllAppointments() {
        return ResponseEntity.ok(appointmentRepository.findAll());
    }
}
