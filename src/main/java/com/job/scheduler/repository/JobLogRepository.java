package com.job.scheduler.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.job.scheduler.entity.JobLog;

public interface JobLogRepository extends JpaRepository<JobLog, Long> {

    List<JobLog> findByJobIdOrderByCreatedAtAsc(Long jobId);
}
