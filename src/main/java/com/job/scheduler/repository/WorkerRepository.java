package com.job.scheduler.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.job.scheduler.entity.Worker;
import com.job.scheduler.enums.WorkerStatus;

public interface WorkerRepository extends JpaRepository<Worker, String> {

    List<Worker> findByStatus(WorkerStatus status);

    List<Worker> findByLastHeartbeatAtBeforeAndStatusNot(Instant threshold, WorkerStatus status);
}
