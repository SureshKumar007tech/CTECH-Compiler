package com.ctechcompiler.backend.service;

import com.ctechcompiler.backend.model.User;
import com.ctechcompiler.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository repo;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository repo, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    // create mentor (bootstrap)
    public User registerMentor(String username, String rawPassword, String name) throws Exception {
        if (repo.existsByUsername(username)) throw new Exception("Username already exists");
        String hashed = passwordEncoder.encode(rawPassword);
        User u = new User(username, hashed, "MENTOR", name);
        return repo.save(u);
    }

    // mentor creates student after verifying mentor credentials
    public User registerStudent(String mentorUsername, String mentorPassword,
                                String studentUsername, String studentPassword, String studentName) throws Exception {
        Optional<User> mentorOpt = repo.findByUsername(mentorUsername);
        if (mentorOpt.isEmpty()) throw new Exception("Mentor not found");
        User mentor = mentorOpt.get();
        if (!mentor.getRole().equals("MENTOR")) throw new Exception("Not a mentor account");
        if (!passwordEncoder.matches(mentorPassword, mentor.getPassword())) throw new Exception("Invalid mentor credentials");

        if (repo.existsByUsername(studentUsername)) throw new Exception("Student username already exists");
        String hashed = passwordEncoder.encode(studentPassword);
        User student = new User(studentUsername, hashed, "STUDENT", studentName);
        return repo.save(student);
    }

    // login
    public Optional<User> login(String username, String rawPassword) {
        Optional<User> u = repo.findByUsername(username);
        if (u.isPresent() && passwordEncoder.matches(rawPassword, u.get().getPassword())) {
            return u;
        }
        return Optional.empty();
    }
}
