package com.job.scheduler.entity;

import java.time.Instant;

import com.job.scheduler.enums.RetryMethods;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "queues")
public class Queue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false)
    private Integer priority = 0;

    @Column(name = "concurrency_limit", nullable = false)
    private Integer concurrencyLimit = 5;

    @Column(nullable = false)
    private Boolean paused = false;

    @Column(name = "default_max_retries", nullable = false)
    private Integer defaultMaxRetries = 3;

    @Enumerated(EnumType.STRING)
    @Column(name = "default_retry_strategy", length = 20)
    private RetryMethods defaultRetryStrategy = RetryMethods.EXPONENTIAL;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public boolean isPaused() {
        return paused;
    }

    public Integer getConcurrencyLimit() {
        return concurrencyLimit;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Integer getDefaultMaxRetries() {
        return defaultMaxRetries;
    }

    public RetryMethods getDefaultRetryStrategy() {
        return defaultRetryStrategy;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Boolean getPaused() {
        return paused;
    }

    public Integer getPriority() {
        return priority;
    }

    public Project getProject() {
        return project;
    }
    public void setConcurrencyLimit(Integer concurrencyLimit) {
        this.concurrencyLimit = concurrencyLimit;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setDefaultMaxRetries(Integer defaultMaxRetries) {
        this.defaultMaxRetries = defaultMaxRetries;
    }

    public void setDefaultRetryStrategy(RetryMethods defaultRetryStrategy) {
        this.defaultRetryStrategy = defaultRetryStrategy;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPaused(Boolean paused) {
        this.paused = paused;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
    
}