package com.example.hospitalmanager.service;

import com.example.hospitalmanager.dto.AppointmentRequest;
import com.example.hospitalmanager.dto.AppointmentResponse;
import com.example.hospitalmanager.exception.ApiException;
import com.example.hospitalmanager.model.AppUser;
import com.example.hospitalmanager.model.Appointment;
import com.example.hospitalmanager.model.AppointmentStatus;
import com.example.hospitalmanager.model.Patient;
import com.example.hospitalmanager.model.Role;
import com.example.hospitalmanager.repository.AppointmentRepository;
import com.example.hospitalmanager.repository.PatientRepository;
import com.example.hospitalmanager.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            PatientRepository patientRepository,
            UserRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
    }

    public AppointmentResponse bookAppointment(AppointmentRequest request) {
        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Patient not found"));
        AppUser doctor = userRepository.findById(request.doctorId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Doctor not found"));

        if (doctor.getRole() != Role.DOCTOR) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Selected user is not a doctor");
        }

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .appointmentTime(request.appointmentTime())
                .status(AppointmentStatus.SCHEDULED)
                .reason(request.reason())
                .build();

        return toResponse(appointmentRepository.save(appointment));
    }

    public List<AppointmentResponse> getAppointmentsForUser(String username) {
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        if (user.getRole() == Role.DOCTOR) {
            return appointmentRepository.findByDoctorId(user.getId()).stream().map(this::toResponse).toList();
        }
        return appointmentRepository.findAll().stream().map(this::toResponse).toList();
    }

    public AppointmentResponse updateStatus(Long id, AppointmentStatus status, String username) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Appointment not found"));

        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        if (user.getRole() == Role.DOCTOR && !appointment.getDoctor().getId().equals(user.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You can only update your own appointments");
        }

        appointment.setStatus(status);
        return toResponse(appointmentRepository.save(appointment));
    }

    private AppointmentResponse toResponse(Appointment a) {
        return new AppointmentResponse(
                a.getId(),
                a.getPatient().getId(),
                a.getPatient().getFullName(),
                a.getDoctor().getId(),
                a.getDoctor().getFullName(),
                a.getAppointmentTime(),
                a.getStatus(),
                a.getReason()
        );
    }
}