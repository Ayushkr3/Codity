package com.job.scheduler.controller;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.job.scheduler.dto.BatchJobRequest;
import com.job.scheduler.dto.CreateJobRequest;
import com.job.scheduler.dto.JobExecutionResponse;
import com.job.scheduler.dto.JobLogResponse;
import com.job.scheduler.dto.JobResponse;
import com.job.scheduler.enums.JobStatus;
import com.job.scheduler.service.JobService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/queues/{queueId}/jobs")
//@RequestMapping("/api/queues/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping
    public ResponseEntity<JobResponse> createJob(Authentication auth,
            @PathVariable Long queueId,
            /*@Valid */ @RequestBody CreateJobRequest request) {
        Long userId = (Long) auth.getPrincipal();
        JobResponse job = jobService.createJob(queueId, request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(job);
    }

    @PostMapping("/batch")
    public ResponseEntity<List<JobResponse>> createBatch(Authentication auth,
            @PathVariable Long queueId,
            @Valid @RequestBody BatchJobRequest request) {
        Long userId = (Long) auth.getPrincipal();
        List<JobResponse> jobs = jobService.createBatch(queueId, request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(jobs);
    }

    @GetMapping
    public ResponseEntity<Page<JobResponse>> listJobs(Authentication auth,
            @PathVariable Long queueId,
            @RequestParam(required = false) JobStatus status,
            Pageable pageable) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(jobService.listJobs(queueId, status, userId, pageable));
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<JobResponse> getJob(Authentication auth,
            @PathVariable Long queueId,
            @PathVariable Long jobId) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(jobService.getJob(queueId, jobId, userId));
    }

    @PostMapping("/{jobId}/retry")
    public ResponseEntity<JobResponse> retryJob(Authentication auth,
            @PathVariable Long queueId,
            @PathVariable Long jobId) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(jobService.retryJob(queueId, jobId, userId));
    }

    @DeleteMapping("/{jobId}")
    public ResponseEntity<Void> cancelJob(Authentication auth,
            @PathVariable Long queueId,
            @PathVariable Long jobId) {
        Long userId = (Long) auth.getPrincipal();
        jobService.cancelJob(queueId, jobId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{jobId}/logs")
    public ResponseEntity<List<JobLogResponse>> getLogs(Authentication auth,
            @PathVariable Long queueId,
            @PathVariable Long jobId) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(jobService.getLogs(queueId, jobId, userId));
    }

    @GetMapping("/{jobId}/executions")
    public ResponseEntity<List<JobExecutionResponse>> getExecutions(Authentication auth,
            @PathVariable Long queueId,
            @PathVariable Long jobId) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(jobService.getExecutions(queueId, jobId, userId));
    }
}
