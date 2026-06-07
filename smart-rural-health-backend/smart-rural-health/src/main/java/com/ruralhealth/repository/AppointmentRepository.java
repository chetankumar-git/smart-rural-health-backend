package com.ruralhealth.repository;

import com.ruralhealth.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatientIdOrderByAppointmentDateDesc(Long patientId);

    List<Appointment> findByDoctorIdOrderByAppointmentDateAsc(Long doctorId);

    List<Appointment> findByDoctorIdAndStatus(
            Long doctorId,
            Appointment.AppointmentStatus status);
}
