package com.job.scheduler.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.job.scheduler.dto.ProjectRequest;
import com.job.scheduler.dto.ProjectResponse;
import com.job.scheduler.entity.Project;
import com.job.scheduler.entity.User;
import com.job.scheduler.repository.ProjectRepository;
import com.job.scheduler.repository.UserRepository;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    public List<ProjectResponse> listForUser(Long userId) {
        return projectRepository.findAllByOwnerId(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ProjectResponse create(Long userId, ProjectRequest request) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Project project = new Project();
        project.setName(request.getName());
        project.setOwner(owner);
        project = projectRepository.save(project);
        return toResponse(project);
    }
    public Project resolveDefaultProject(Long userId) {
        return projectRepository.findByOwnerId(userId)
                .orElseThrow(() -> new IllegalStateException("Project not found"));
    }

    private ProjectResponse toResponse(Project project) {
        ProjectResponse resp = new ProjectResponse();
        resp.setId(project.getId());
        resp.setName(project.getName());
        resp.setCreatedAt(project.getCreatedAt().toString());
        return resp;
    }
}
