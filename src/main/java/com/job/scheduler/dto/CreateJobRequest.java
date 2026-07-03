package com.job.scheduler.dto;

import com.job.scheduler.enums.RetryMethods;

import jakarta.validation.constraints.NotBlank;

public class CreateJobRequest {

    @NotBlank
    String type;

    String payLoad;
    Integer priority = null;
    Integer DelaySecond = null;
    String AtTime = null;
    String CronExp = null;
    Integer maxRetries = null;
    RetryMethods retryStrategy = null;

    public String getPayLoad() {
        return payLoad;
    }

    public void setPayLoad(String payLoad) {
        this.payLoad = payLoad;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAtTime() {
        return AtTime;
    }

    public void setAtTime(String atTime) {
        AtTime = atTime;
    }

    public String getCronExp() {
        return CronExp;
    }

    public void setCronExp(String cronExp) {
        CronExp = cronExp;
    }

    public Integer getDelaySecond() {
        return DelaySecond;
    }

    public void setDelaySecond(Integer delaySecond) {
        DelaySecond = delaySecond;
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
