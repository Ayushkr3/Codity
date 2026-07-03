package com.job.scheduler.dto;

import com.job.scheduler.enums.WorkerStatus;

public class WorkerResponse {

    private String workerId;
    private String hostname;
    private WorkerStatus status;
    private Integer activeJobCount;
    private String startedAt;
    private String lastHeartbeatAt;

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

    public String getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(String startedAt) {
        this.startedAt = startedAt;
    }

    public String getLastHeartbeatAt() {
        return lastHeartbeatAt;
    }

    public void setLastHeartbeatAt(String lastHeartbeatAt) {
        this.lastHeartbeatAt = lastHeartbeatAt;
    }
}
