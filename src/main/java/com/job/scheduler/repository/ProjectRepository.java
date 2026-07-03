package com.job.scheduler.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.job.scheduler.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    Optional<Project> findByOwnerId(Long ownerId);

    List<Project> findAllByOwnerId(Long ownerId);

}