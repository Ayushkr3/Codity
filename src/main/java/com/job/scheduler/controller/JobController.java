package com.job.scheduler.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.job.scheduler.dto.CreateJobRequest;
import com.job.scheduler.dto.JobResponse;
import com.job.scheduler.entity.User;
import com.job.scheduler.service.JobService;

import jakarta.validation.Valid;

@RestController
//@RequestMapping("/api/queues/{queueId}/jobs")
@RequestMapping("/api/queues/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping
    public ResponseEntity<JobResponse> createJob(Authentication auth,
            //@PathVariable Long queueId,
            @Valid @RequestBody CreateJobRequest request) {
                
            Long userId = (Long) auth.getPrincipal();
            Long queueId =1l;
            if(request.getAtTime()==null&& request.getCronExp()==null && request.getDelaySecond()==null){
                JobResponse js = new JobResponse();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(js);
            }
            JobResponse job = jobService.createJob(queueId, request,userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(job);
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<JobResponse> getJob(
            @PathVariable Long queueId,
            @PathVariable Long jobId) {
        return ResponseEntity.ok(jobService.getJob(queueId, jobId));
    }

    // @GetMapping
    // public ResponseEntity<Page<JobResponse>> listJobs(
    //         @PathVariable Long queueId,
    //         @RequestParam(required = false) String status,
    //         Pageable pageable) {
    //     return ResponseEntity.ok(jobService.listJobs(queueId, status, pageable));
    // }

    // @PostMapping("/{jobId}/retry")
    // public ResponseEntity<JobResponse> retryJob(
    //         @PathVariable Long queueId,
    //         @PathVariable Long jobId) {
    //     return ResponseEntity.ok(jobService.retryJob(queueId, jobId));
    // }

    // @DeleteMapping("/{jobId}")
    // public ResponseEntity<Void> cancelJob(
    //         @PathVariable Long queueId,
    //         @PathVariable Long jobId) {
    //     jobService.cancelJob(queueId, jobId);
    //     return ResponseEntity.noContent().build();
    // }
}
