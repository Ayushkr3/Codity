package com.job.scheduler.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.job.scheduler.entity.Project;
import com.job.scheduler.entity.User;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    Optional<Project> findByOwnerId(Long ownerId);

}