package com.job.scheduler.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public class BatchJobRequest {

    @NotEmpty
    @Valid
    private List<CreateJobRequest> jobs;

    public List<CreateJobRequest> getJobs() {
        return jobs;
    }

    public void setJobs(List<CreateJobRequest> jobs) {
        this.jobs = jobs;
    }
}
