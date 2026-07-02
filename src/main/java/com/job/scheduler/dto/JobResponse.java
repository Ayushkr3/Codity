package com.job.scheduler.dto;

import com.job.scheduler.enums.JobStatus;

public class JobResponse {
    Long id;
    String type;
    JobStatus status;
    String createdAt;

    public String getCreatedAt() {
        return createdAt;
    }

    public Long getId() {
        return id;
    }

    public JobStatus getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public void setType(String type) {
        this.type = type;
    }
}