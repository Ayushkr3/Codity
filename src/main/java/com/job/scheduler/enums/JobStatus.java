package com.job.scheduler.enums;

public enum JobStatus {
    QUEUED,
    SCHEDULED,
    CLAIMED,
    RUNNING,
    COMPLETED,
    FAILED,
    DEAD_LETTER,
    CANCELED
}