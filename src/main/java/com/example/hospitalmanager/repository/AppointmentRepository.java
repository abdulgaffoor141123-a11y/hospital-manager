package com.example.hospitalmanager.repository;

import com.example.hospitalmanager.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByDoctorId(Long doctorId);
    boolean existsByPatientId(Long patientId);
}