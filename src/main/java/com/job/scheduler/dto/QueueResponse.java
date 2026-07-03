package com.job.scheduler.dto;

import java.util.Map;

import com.job.scheduler.enums.RetryMethods;

public class QueueResponse {

    private Long id;
    private Long projectId;
    private String name;
    private Integer priority;
    private Integer concurrencyLimit;
    private Boolean paused;
    private Integer defaultMaxRetries;
    private RetryMethods defaultRetryStrategy;
    private Map<String, Long> jobCountsByStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getConcurrencyLimit() {
        return concurrencyLimit;
    }

    public void setConcurrencyLimit(Integer concurrencyLimit) {
        this.concurrencyLimit = concurrencyLimit;
    }

    public Boolean getPaused() {
        return paused;
    }

    public void setPaused(Boolean paused) {
        this.paused = paused;
    }

    public Integer getDefaultMaxRetries() {
        return defaultMaxRetries;
    }

    public void setDefaultMaxRetries(Integer defaultMaxRetries) {
        this.defaultMaxRetries = defaultMaxRetries;
    }

    public RetryMethods getDefaultRetryStrategy() {
        return defaultRetryStrategy;
    }

    public void setDefaultRetryStrategy(RetryMethods defaultRetryStrategy) {
        this.defaultRetryStrategy = defaultRetryStrategy;
    }

    public Map<String, Long> getJobCountsByStatus() {
        return jobCountsByStatus;
    }

    public void setJobCountsByStatus(Map<String, Long> jobCountsByStatus) {
        this.jobCountsByStatus = jobCountsByStatus;
    }
}
