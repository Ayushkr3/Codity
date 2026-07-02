package com.job.scheduler.handler;

public interface JobHandler {
    void handle(String payload) throws Exception;
}