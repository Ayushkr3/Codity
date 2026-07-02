package com.job.scheduler.dispatcher;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.job.scheduler.repository.JobRepository;

@Component
public class JobPoller {

    private final JobRepository jobRepository;
    // private final JobExecutor jobExecutor;

    public JobPoller(JobRepository jobRepository /*,JobExecutor jobExecutor*/) {
        this.jobRepository = jobRepository;
    //    this.jobExecutor = jobExecutor;
    }

    @Scheduled(fixedDelay = 500)
    public void pollAndClaim() {
        //List<Job> claimed = jobRepository.claimNextJobs(workerId, 10);
        //claimed.forEach(jobExecutor::submit);
    }
}