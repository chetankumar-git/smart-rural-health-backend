package com.ruralhealth.controller;

import com.ruralhealth.model.User;
import com.ruralhealth.repository.*;
import com.ruralhealth.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private UserRepository userRepository;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private AuthenticationManager authenticationManager;

    // ── Register ──────────────────────────────────────────────────
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        if (userRepository.existsByMobile(req.getMobile())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Mobile number already registered"));
        }

        User user = User.builder()
                .fullName(req.getFullName())
                .mobile(req.getMobile())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(req.getRole() != null ? req.getRole() : User.Role.PATIENT)
                .age(req.getAge())
                .village(req.getVillage())
                .district(req.getDistrict())
                .state(req.getState())
                .language(req.getLanguage() != null ? req.getLanguage() : "English")
                .enabled(true)
                .build();

        userRepository.save(user);
        String token = jwtUtil.generateToken(user.getMobile(), user.getRole().name());

        return ResponseEntity.ok(Map.of(
                "message", "Registration successful",
                "token", token,
                "role", user.getRole(),
                "name", user.getFullName()
        ));
    }

    // ── Login ─────────────────────────────────────────────────────
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
User user = userRepository.findByMobile(req.getMobile())
        .orElseThrow(() -> new RuntimeException("User not found"));

if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(Map.of("error", "Invalid mobile or password"));
}

        String token = jwtUtil.generateToken(user.getMobile(), user.getRole().name());

        return ResponseEntity.ok(Map.of(
                "token", token,
                "role", user.getRole(),
                "name", user.getFullName(),
                "id", user.getId()
        ));
    }

    // ── DTOs ──────────────────────────────────────────────────────
    @Data
    static class RegisterRequest {
        @jakarta.validation.constraints.NotBlank String fullName;
        @jakarta.validation.constraints.NotBlank String mobile;
        String email;
        @jakarta.validation.constraints.NotBlank String password;
        User.Role role;
        Integer age;
        String village;
        String district;
        String state;
        String language;
    }

    @Data
    static class LoginRequest {
        String mobile;
        String password;
    }
}
