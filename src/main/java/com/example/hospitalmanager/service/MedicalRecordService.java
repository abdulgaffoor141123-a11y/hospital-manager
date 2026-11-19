package com.example.hospitalmanager.service;

import com.example.hospitalmanager.dto.MedicalRecordRequest;
import com.example.hospitalmanager.dto.MedicalRecordResponse;
import com.example.hospitalmanager.exception.ApiException;
import com.example.hospitalmanager.model.AppUser;
import com.example.hospitalmanager.model.MedicalRecord;
import com.example.hospitalmanager.model.Patient;
import com.example.hospitalmanager.repository.MedicalRecordRepository;
import com.example.hospitalmanager.repository.PatientRepository;
import com.example.hospitalmanager.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    public MedicalRecordService(
            MedicalRecordRepository medicalRecordRepository,
            PatientRepository patientRepository,
            UserRepository userRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
    }

    public MedicalRecordResponse createRecord(MedicalRecordRequest request, String doctorUsername) {
        AppUser doctor = userRepository.findByUsername(doctorUsername)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Doctor not found"));
        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Patient not found"));

        MedicalRecord record = MedicalRecord.builder()
                .patient(patient)
                .doctor(doctor)
                .diagnosis(request.diagnosis())
                .prescription(request.prescription())
                .notes(request.notes())
                .createdAt(LocalDateTime.now())
                .build();

        return toResponse(medicalRecordRepository.save(record));
    }

    public List<MedicalRecordResponse> getRecordsByPatient(Long patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Patient not found");
        }
        return medicalRecordRepository.findByPatientId(patientId).stream().map(this::toResponse).toList();
    }

    private MedicalRecordResponse toResponse(MedicalRecord r) {
        return new MedicalRecordResponse(
                r.getId(),
                r.getPatient().getId(),
                r.getPatient().getFullName(),
                r.getDoctor().getId(),
                r.getDoctor().getFullName(),
                r.getDiagnosis(),
                r.getPrescription(),
                r.getNotes(),
                r.getCreatedAt()
        );
    }
}