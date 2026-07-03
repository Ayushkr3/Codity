package com.job.scheduler.dto;

import com.job.scheduler.enums.RetryMethods;

import jakarta.validation.constraints.NotBlank;

public class QueueRequest {

    @NotBlank
    private String name;

    private Integer priority;
    private Integer concurrencyLimit;
    private Integer defaultMaxRetries;
    private RetryMethods defaultRetryStrategy;

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
}
