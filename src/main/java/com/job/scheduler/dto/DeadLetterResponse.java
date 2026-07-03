package com.job.scheduler.dto;

public class DeadLetterResponse {

    private Long id;
    private Long jobId;
    private String jobType;
    private String reason;
    private String payloadSnapshot;
    private Integer retryCountAtFailure;
    private String failedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
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

    public String getFailedAt() {
        return failedAt;
    }

    public void setFailedAt(String failedAt) {
        this.failedAt = failedAt;
    }
}
