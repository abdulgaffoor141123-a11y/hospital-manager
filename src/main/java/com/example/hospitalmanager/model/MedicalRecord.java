package com.example.hospitalmanager.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "medical_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    private AppUser doctor;

    @Column(nullable = false, length = 1000)
    private String diagnosis;

    @Column(nullable = false, length = 1000)
    private String prescription;

    private String notes;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}