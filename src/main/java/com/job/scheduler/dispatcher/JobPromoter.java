package com.job.scheduler.dispatcher;

import java.time.Instant;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.job.scheduler.repository.JobRepository;

@Component
//@ConditionalOnProperty(name = "app.mode", havingValue = "worker")
public class JobPromoter {

    private final JobRepository jobRepository;

    public JobPromoter(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void promoteReadyJobs() {
        int promoted = jobRepository.promoteScheduledJobsToQueued(Instant.now());
        if (promoted > 0) {
            
        }
    }
}