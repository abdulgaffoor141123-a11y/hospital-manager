package com.example.hospitalmanager.service;

import com.example.hospitalmanager.exception.ApiException;
import com.example.hospitalmanager.model.Patient;
import com.example.hospitalmanager.repository.AppointmentRepository;
import com.example.hospitalmanager.repository.MedicalRecordRepository;
import com.example.hospitalmanager.repository.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    public PatientService(
            PatientRepository patientRepository,
            AppointmentRepository appointmentRepository,
            MedicalRecordRepository medicalRecordRepository) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.medicalRecordRepository = medicalRecordRepository;
    }

    public List<Patient> getAllPatients(String name) {
        if (name != null && !name.isBlank()) {
            return patientRepository.findByFullNameContainingIgnoreCase(name.trim());
        }
        return patientRepository.findAll();
    }

    public Patient getPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Patient not found"));
    }

    public Patient createPatient(Patient patient) {
        patient.setId(null);
        return patientRepository.save(patient);
    }

    public Patient updatePatient(Long id, Patient updated) {
        Patient existing = getPatientById(id);
        existing.setFullName(updated.getFullName());
        existing.setAge(updated.getAge());
        existing.setGender(updated.getGender());
        existing.setPhone(updated.getPhone());
        existing.setEmail(updated.getEmail());
        existing.setAddress(updated.getAddress());
        return patientRepository.save(existing);
    }

    public void deletePatient(Long id) {
        Patient existing = getPatientById(id);
        if (appointmentRepository.existsByPatientId(id) || medicalRecordRepository.existsByPatientId(id)) {
            throw new ApiException(HttpStatus.CONFLICT, "Cannot delete patient with existing appointments or medical records");
        }
        patientRepository.delete(existing);
    }
}