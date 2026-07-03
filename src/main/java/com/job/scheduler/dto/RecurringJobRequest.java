package com.job.scheduler.dto;

import com.job.scheduler.enums.RetryMethods;

import jakarta.validation.constraints.NotBlank;

public class RecurringJobRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String type;

    private String payload;

    @NotBlank
    private String cronExpression;

    private Integer priority;
    private Integer maxRetries;
    private RetryMethods retryStrategy;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(Integer maxRetries) {
        this.maxRetries = maxRetries;
    }

    public RetryMethods getRetryStrategy() {
        return retryStrategy;
    }

    public void setRetryStrategy(RetryMethods retryStrategy) {
        this.retryStrategy = retryStrategy;
    }
}
