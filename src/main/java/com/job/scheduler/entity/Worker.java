package com.job.scheduler.entity;

import java.time.Instant;

import com.job.scheduler.enums.WorkerStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "workers")
public class Worker {

    @Id
    @Column(name = "worker_id", length = 64)
    private String workerId;

    @Column(name = "hostname")
    private String hostname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WorkerStatus status = WorkerStatus.ACTIVE;

    @Column(name = "active_job_count", nullable = false)
    private Integer activeJobCount = 0;

    @Column(name = "started_at", nullable = false, updatable = false)
    private Instant startedAt = Instant.now();

    @Column(name = "last_heartbeat_at")
    private Instant lastHeartbeatAt = Instant.now();

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public WorkerStatus getStatus() {
        return status;
    }

    public void setStatus(WorkerStatus status) {
        this.status = status;
    }

    public Integer getActiveJobCount() {
        return activeJobCount;
    }

    public void setActiveJobCount(Integer activeJobCount) {
        this.activeJobCount = activeJobCount;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getLastHeartbeatAt() {
        return lastHeartbeatAt;
    }

    public void setLastHeartbeatAt(Instant lastHeartbeatAt) {
        this.lastHeartbeatAt = lastHeartbeatAt;
    }
}
