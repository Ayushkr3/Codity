package com.job.scheduler.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;


@Entity
@Table(name = "worker_heartbeats", indexes = {
    @Index(name = "idx_heartbeats_worker", columnList = "worker_id, heartbeat_at")
})
public class WorkerHeartbeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "worker_id", nullable = false, length = 64)
    private String workerId;

    @Column(name = "heartbeat_at", nullable = false)
    private Instant heartbeatAt = Instant.now();

    @Column(name = "active_job_count", nullable = false)
    private Integer activeJobCount = 0;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public Instant getHeartbeatAt() {
        return heartbeatAt;
    }

    public void setHeartbeatAt(Instant heartbeatAt) {
        this.heartbeatAt = heartbeatAt;
    }

    public Integer getActiveJobCount() {
        return activeJobCount;
    }

    public void setActiveJobCount(Integer activeJobCount) {
        this.activeJobCount = activeJobCount;
    }
}
