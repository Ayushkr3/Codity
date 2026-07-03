package com.job.scheduler.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.job.scheduler.dto.RecurringJobRequest;
import com.job.scheduler.dto.RecurringJobResponse;
import com.job.scheduler.service.RecurringJobService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/queues/{queueId}/recurring-jobs")
public class RecurringJobController {

    private final RecurringJobService recurringJobService;

    public RecurringJobController(RecurringJobService recurringJobService) {
        this.recurringJobService = recurringJobService;
    }

    @PostMapping
    public ResponseEntity<RecurringJobResponse> create(Authentication auth,
            @PathVariable Long queueId,
            @Valid @RequestBody RecurringJobRequest request) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(recurringJobService.create(userId, queueId, request));
    }

    @GetMapping
    public ResponseEntity<List<RecurringJobResponse>> list(Authentication auth,
            @PathVariable Long queueId) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(recurringJobService.listForQueue(userId, queueId));
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Void> delete(
            Authentication auth,
            @PathVariable Long queueId,
            @PathVariable String name) {

        Long userId = (Long) auth.getPrincipal();

        if (recurringJobService.deleteRecurringJob(userId, queueId, name)) {
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/{id}/pause")
    public ResponseEntity<RecurringJobResponse> pause(Authentication auth,
            @PathVariable Long queueId,
            @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(recurringJobService.setEnabled(userId, id, false));
    }

    @PostMapping("/{id}/resume")
    public ResponseEntity<RecurringJobResponse> resume(Authentication auth,
            @PathVariable Long queueId,
            @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(recurringJobService.setEnabled(userId, id, true));
    }
}
