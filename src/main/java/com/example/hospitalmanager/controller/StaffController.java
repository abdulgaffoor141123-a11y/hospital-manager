package com.example.hospitalmanager.controller;

import com.example.hospitalmanager.dto.UserResponse;
import com.example.hospitalmanager.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class StaffController {

    private final AuthService authService;

    public StaffController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getUsers() {
        return ResponseEntity.ok(authService.getAllUsers());
    }

    @GetMapping("/doctors")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE', 'RECEPTIONIST', 'PATIENT')")
    public ResponseEntity<List<UserResponse>> getDoctors() {
        return ResponseEntity.ok(authService.getDoctors());
    }
}
