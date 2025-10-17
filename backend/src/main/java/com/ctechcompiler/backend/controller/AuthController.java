package com.ctechcompiler.backend.controller;

import com.ctechcompiler.backend.dto.AuthRequest;
import com.ctechcompiler.backend.dto.AuthResponse;
import com.ctechcompiler.backend.dto.RegisterStudentRequest;
import com.ctechcompiler.backend.model.User;
import com.ctechcompiler.backend.service.AuthService;
import com.ctechcompiler.backend.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // For development, allow all origins
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/register-student")
    public ResponseEntity<?> registerStudent(@RequestBody RegisterStudentRequest request) {
        try {
            User student = authService.registerStudent(
                    request.mentorUsername,
                    request.mentorPassword,
                    request.studentUsername,
                    request.studentPassword,
                    request.studentName
            );
            return ResponseEntity.ok("Student " + student.getUsername() + " registered successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        Optional<User> userOptional = authService.login(request.getUsername(), request.getPassword());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String token = jwtService.generateToken(user);

            // This line correctly uses the 4-argument constructor, including the token
            AuthResponse response = new AuthResponse(user.getUsername(), user.getRole(), user.getName(), token);

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @PostMapping("/register-mentor")
    public ResponseEntity<?> registerMentor(@RequestBody AuthRequest request) {
        try {
            User mentor = authService.registerMentor(request.getUsername(), request.getPassword(), request.getName());
            return ResponseEntity.ok("Mentor " + mentor.getUsername() + " registered successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}

