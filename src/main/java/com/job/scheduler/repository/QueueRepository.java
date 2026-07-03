package com.job.scheduler.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.job.scheduler.entity.Queue;

public interface QueueRepository extends JpaRepository<Queue, Long> {

    List<Queue> findByProjectId(Long projectId);

}