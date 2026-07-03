package com.job.scheduler.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.job.scheduler.dto.DeadLetterResponse;
import com.job.scheduler.dto.JobResponse;
import com.job.scheduler.service.JobService;

@RestController
@RequestMapping("/api/dlq")

public class DeadLetterController {

    private final JobService jobService;

    public DeadLetterController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping
    public ResponseEntity<Page<DeadLetterResponse>> list(Authentication auth, Pageable pageable) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(jobService.listDeadLetters(userId, pageable));
    }

    @PostMapping("/{id}/requeue")
    public ResponseEntity<JobResponse> requeue(Authentication auth, @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(jobService.requeueFromDeadLetter(id, userId));
    }
}
