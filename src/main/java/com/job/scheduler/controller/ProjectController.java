package com.job.scheduler.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.job.scheduler.dto.ProjectRequest;
import com.job.scheduler.dto.ProjectResponse;
import com.job.scheduler.service.ProjectService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> listProjects(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(projectService.listForUser(userId));
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(Authentication auth,
            @Valid @RequestBody ProjectRequest request) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.create(userId, request));
    }
}
