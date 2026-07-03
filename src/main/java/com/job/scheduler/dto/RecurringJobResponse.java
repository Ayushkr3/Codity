package com.job.scheduler.dto;

public class RecurringJobResponse {

    private Long id;
    private Long queueId;
    private String name;
    private String type;
    private String cronExpression;
    private Boolean enabled;
    private String nextRunAt;
    private String lastRunAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getQueueId() {
        return queueId;
    }

    public void setQueueId(Long queueId) {
        this.queueId = queueId;
    }

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

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getNextRunAt() {
        return nextRunAt;
    }

    public void setNextRunAt(String nextRunAt) {
        this.nextRunAt = nextRunAt;
    }

    public String getLastRunAt() {
        return lastRunAt;
    }

    public void setLastRunAt(String lastRunAt) {
        this.lastRunAt = lastRunAt;
    }
}
