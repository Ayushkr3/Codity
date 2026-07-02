package com.job.scheduler.service;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.job.scheduler.dto.AuthResponse;
import com.job.scheduler.dto.LoginRequest;
import com.job.scheduler.dto.RegisterRequest;
import com.job.scheduler.entity.Project;
import com.job.scheduler.entity.User;
import com.job.scheduler.repository.ProjectRepository;
import com.job.scheduler.repository.UserRepository;
import com.job.scheduler.security.JwtUtil;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository,ProjectRepository projectRepo, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.projectRepo = projectRepo;
    }

    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalStateException("Email already registered");
        }

        User user = new User();
        Project pr = new Project();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));

        user = userRepository.save(user);
        pr.setName(UUID.randomUUID().toString());
        pr.setOwner(user);
        projectRepo.save(pr);
        String token = jwtUtil.generateToken(user.getId(), user.getEmail());
        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail());
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getEmail());
        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail());
    }
}