package com.ruralhealth.repository;

import com.ruralhealth.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    List<Feedback> findByDoctorId(Long doctorId);

    boolean existsByPatientIdAndAppointmentId(
            Long patientId,
            Long appointmentId);
}
