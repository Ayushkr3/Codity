package com.job.scheduler.dispatcher;

import java.util.List;
import java.util.UUID;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.job.scheduler.entity.Job;
import com.job.scheduler.repository.JobRepository;

import jakarta.transaction.Transactional;

@Component
public class JobPoller {

    private final JobRepository jobRepository;
    private final JobExecutor jobExecutor;
    private final String workerId = UUID.randomUUID().toString();

    public JobPoller(JobRepository jobRepository, JobExecutor jobExecutor) {
        this.jobRepository = jobRepository;
        this.jobExecutor = jobExecutor;
    }

    @Scheduled(fixedDelay = 500)
    @Transactional
    public void pollAndClaim() {
        List<Long> claimedIds = jobRepository.claimNextJobIds(workerId, 10);
        if (claimedIds.isEmpty()) return;

        List<Job> claimedJobs = jobRepository.findByIdIn(claimedIds);
        claimedJobs.forEach(jobExecutor::submit);
    }
}