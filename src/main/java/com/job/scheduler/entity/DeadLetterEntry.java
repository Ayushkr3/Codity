package com.job.scheduler.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;


@Entity
@Table(name = "dead_letter_entries", indexes = {
    @Index(name = "idx_dlq_job", columnList = "job_id")
})
public class DeadLetterEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false, unique = true)
    private Job job;

    @Column(nullable = false, columnDefinition = "text")
    private String reason;

    @Column(name = "payload_snapshot", columnDefinition = "text")
    private String payloadSnapshot;

    @Column(name = "retry_count_at_failure", nullable = false)
    private Integer retryCountAtFailure;

    @Column(name = "failed_at", nullable = false, updatable = false)
    private Instant failedAt = Instant.now();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getPayloadSnapshot() {
        return payloadSnapshot;
    }

    public void setPayloadSnapshot(String payloadSnapshot) {
        this.payloadSnapshot = payloadSnapshot;
    }

    public Integer getRetryCountAtFailure() {
        return retryCountAtFailure;
    }

    public void setRetryCountAtFailure(Integer retryCountAtFailure) {
        this.retryCountAtFailure = retryCountAtFailure;
    }

    public Instant getFailedAt() {
        return failedAt;
    }

    public void setFailedAt(Instant failedAt) {
        this.failedAt = failedAt;
    }
}
