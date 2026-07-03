package com.job.scheduler.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.job.scheduler.entity.WorkerHeartbeat;

public interface WorkerHeartbeatRepository extends JpaRepository<WorkerHeartbeat, Long> {

    Page<WorkerHeartbeat> findByWorkerIdOrderByHeartbeatAtDesc(String workerId, Pageable pageable);
}
