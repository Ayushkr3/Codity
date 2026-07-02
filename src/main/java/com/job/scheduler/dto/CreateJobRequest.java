package com.job.scheduler.dto;

public class CreateJobRequest {
    String type;
    String payLoad;
    Integer priority = null;
    Integer DelaySecond = null;
    String AtTime = null;
    String CronExp = null;
    public String getPayLoad() {
        return payLoad;
    }

    public Integer getPriority() {
        return priority;
    }

    public String getType() {
        return type;
    }

    public String getAtTime() {
        return AtTime;
    }

    public String getCronExp() {
        return CronExp;
    }

    public Integer getDelaySecond() {
        return DelaySecond;
    }

}
