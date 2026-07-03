package com.job.scheduler.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.job.scheduler.entity.JobExecution;

public interface JobExecutionRepository extends JpaRepository<JobExecution, Long> {

    List<JobExecution> findByJobIdOrderByAttemptNumberAsc(Long jobId);

    int countByJobId(Long jobId);
}
