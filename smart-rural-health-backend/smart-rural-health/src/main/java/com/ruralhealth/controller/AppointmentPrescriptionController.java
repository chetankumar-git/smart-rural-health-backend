package com.ruralhealth.controller;

import com.ruralhealth.model.*;
import com.ruralhealth.repository.*;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

// ═══════════════════════════════════════════════════════════════
//  APPOINTMENT CONTROLLER
// ═══════════════════════════════════════════════════════════════
@RestController
@RequestMapping("/api/appointments")
class AppointmentController {

    @Autowired private AppointmentRepository appointmentRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private DoctorRepository doctorRepository;

    // Book appointment (Patient)
    @PreAuthorize("hasRole('PATIENT')")
    @PostMapping("/book")
    public ResponseEntity<?> bookAppointment(@RequestBody BookingRequest req) {
        User patient = userRepository.findById(req.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        Doctor doctor = doctorRepository.findById(req.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Appointment appt = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .appointmentDate(LocalDate.parse(req.getDate()))
                .appointmentTime(LocalTime.parse(req.getTime()))
                .symptoms(req.getSymptoms())
                .notes(req.getNotes())
                .mode(Appointment.ConsultationMode.valueOf(req.getMode()))
                .status(Appointment.AppointmentStatus.PENDING)
                .build();

        appointmentRepository.save(appt);
        return ResponseEntity.ok(Map.of(
                "message", "Appointment booked successfully! Confirmation sent on WhatsApp.",
                "appointmentId", appt.getId()
        ));
    }

    // Get patient's appointments
    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> getPatientAppointments(@PathVariable Long patientId) {
        return ResponseEntity.ok(appointmentRepository.findByPatientIdOrderByAppointmentDateDesc(patientId));
    }

    // Get doctor's appointments
    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<?> getDoctorAppointments(@PathVariable Long doctorId) {
        return ResponseEntity.ok(appointmentRepository.findByDoctorIdOrderByAppointmentDateAsc(doctorId));
    }

    // Confirm or Cancel appointment (Doctor)
    @PreAuthorize("hasRole('DOCTOR')")
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id,
                                           @RequestBody Map<String, String> body) {
        return appointmentRepository.findById(id).map(appt -> {
            appt.setStatus(Appointment.AppointmentStatus.valueOf(body.get("status")));
            appointmentRepository.save(appt);
            return ResponseEntity.ok(Map.of("message", "Status updated to " + body.get("status")));
        }).orElse(ResponseEntity.notFound().build());
    }

    @Data
    static class BookingRequest {
        Long patientId;
        Long doctorId;
        String date;    // "2025-06-10"
        String time;    // "10:00"
        String symptoms;
        String notes;
        String mode;    // "IN_PERSON" | "VIDEO" | "PHONE"
    }
}

// ═══════════════════════════════════════════════════════════════
//  PRESCRIPTION CONTROLLER
// ═══════════════════════════════════════════════════════════════
@RestController
@RequestMapping("/api/prescriptions")
class PrescriptionController {

    @Autowired private PrescriptionRepository prescriptionRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private DoctorRepository doctorRepository;
    @Autowired private AppointmentRepository appointmentRepository;

    // Create prescription (Doctor only)
    @PreAuthorize("hasRole('DOCTOR')")
    @PostMapping("/create")
    public ResponseEntity<?> createPrescription(@RequestBody PrescriptionRequest req) {
        User patient = userRepository.findById(req.getPatientId()).orElseThrow();
        Doctor doctor = doctorRepository.findById(req.getDoctorId()).orElseThrow();

        Prescription rx = Prescription.builder()
                .patient(patient)
                .doctor(doctor)
                .diagnosis(req.getDiagnosis())
                .medicines(req.getMedicines())
                .doctorNotes(req.getDoctorNotes())
                .build();

        prescriptionRepository.save(rx);
        return ResponseEntity.ok(Map.of(
                "message", "Prescription created",
                "rxNumber", rx.getRxNumber(),
                "id", rx.getId()
        ));
    }

    // Get patient's prescriptions
    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> getPatientPrescriptions(@PathVariable Long patientId) {
        return ResponseEntity.ok(prescriptionRepository.findByPatientIdOrderByCreatedAtDesc(patientId));
    }

    // Get prescription by Rx number (for pharmacy QR verification)
    @GetMapping("/verify/{rxNumber}")
    public ResponseEntity<?> verifyPrescription(@PathVariable String rxNumber) {
        return prescriptionRepository.findByRxNumber(rxNumber)
                .map(rx -> ResponseEntity.ok(rx))
                .orElse(ResponseEntity.notFound().build());
    }

    @Data
    static class PrescriptionRequest {
        Long patientId;
        Long doctorId;
        Long appointmentId;
        String diagnosis;
        String medicines;   // JSON string
        String doctorNotes;
    }
}
