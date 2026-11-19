package com.example.hospitalmanager.config;

import com.example.hospitalmanager.model.AppUser;
import com.example.hospitalmanager.model.Patient;
import com.example.hospitalmanager.model.Role;
import com.example.hospitalmanager.repository.PatientRepository;
import com.example.hospitalmanager.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(
            UserRepository userRepository,
            PatientRepository patientRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedUser("admin", "admin123", "System Administrator", Role.ADMIN);
        seedUser("doctor1", "doctor123", "Dr. Nimal Perera", Role.DOCTOR);
        seedUser("nurse1", "nurse123", "Kamani Silva", Role.NURSE);
        seedUser("reception1", "reception123", "Kasun Fernando", Role.RECEPTIONIST);

        if (patientRepository.count() == 0) {
            patientRepository.save(Patient.builder()
                    .fullName("Kasun Jayasinghe")
                    .age(28)
                    .gender("Male")
                    .phone("0771234567")
                    .email("kasun@example.com")
                    .address("Galle, Sri Lanka")
                    .build());

            patientRepository.save(Patient.builder()
                    .fullName("Nethmi Wickramasinghe")
                    .age(34)
                    .gender("Female")
                    .phone("0719876543")
                    .email("nethmi@example.com")
                    .address("Kelaniya, Sri Lanka")
                    .build());
        }
    }

    private void seedUser(String username, String rawPassword, String fullName, Role role) {
        if (!userRepository.existsByUsername(username)) {
            AppUser user = AppUser.builder()
                    .username(username)
                    .password(passwordEncoder.encode(rawPassword))
                    .fullName(fullName)
                    .role(role)
                    .build();
            userRepository.save(user);
        }
    }
}
 

