package com.job.scheduler.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.job.scheduler.dto.QueueRequest;
import com.job.scheduler.dto.QueueResponse;
import com.job.scheduler.service.QueueService;

import jakarta.validation.Valid;

@RestController
public class QueueController {

    private final QueueService queueService;

    public QueueController(QueueService queueService) {
        this.queueService = queueService;
    }

    @PostMapping("/api/projects/{projectId}/queues")
    public ResponseEntity<QueueResponse> createQueue(Authentication auth,
            @PathVariable Long projectId,
            @Valid @RequestBody QueueRequest request) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.status(HttpStatus.CREATED).body(queueService.create(userId, projectId, request));
    }

    @GetMapping("/api/projects/{projectId}/queues")
    public ResponseEntity<List<QueueResponse>> listQueues(Authentication auth,
            @PathVariable Long projectId) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(queueService.listForProject(userId, projectId));
    }

    @GetMapping("/api/queues/{queueId}")
    public ResponseEntity<QueueResponse> getQueue(Authentication auth,
            @PathVariable Long queueId) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(queueService.get(userId, queueId));
    }

    @PatchMapping("/api/queues/{queueId}")
    public ResponseEntity<QueueResponse> updateQueue(Authentication auth,
            @PathVariable Long queueId,
            @RequestBody QueueRequest request) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(queueService.update(userId, queueId, request));
    }

    @PostMapping("/api/queues/{queueId}/pause")
    public ResponseEntity<QueueResponse> pauseQueue(Authentication auth,
            @PathVariable Long queueId) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(queueService.setPaused(userId, queueId, true));
    }

    @PostMapping("/api/queues/{queueId}/resume")
    public ResponseEntity<QueueResponse> resumeQueue(Authentication auth,
            @PathVariable Long queueId) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(queueService.setPaused(userId, queueId, false));
    }

    @GetMapping("/api/queues/{queueId}/stats")
    public ResponseEntity<QueueResponse> queueStats(Authentication auth,
            @PathVariable Long queueId) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(queueService.get(userId, queueId));
    }
}
