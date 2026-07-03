package com.job.scheduler.dispatcher;

import java.net.InetAddress;
import java.util.List;
import java.util.UUID;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.job.scheduler.entity.Job;
import com.job.scheduler.repository.JobRepository;
import com.job.scheduler.service.WorkerService;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.transaction.Transactional;


@Component
@ConditionalOnProperty(name = "app.mode", havingValue = "worker")
public class JobPoller {

    private static final int BATCH_SIZE = 10;

    private final JobRepository jobRepository;
    private final JobExecutor jobExecutor;
    private final WorkerService workerService;
    private final String workerId = UUID.randomUUID().toString();

    public JobPoller(JobRepository jobRepository, JobExecutor jobExecutor, WorkerService workerService) {
        this.jobRepository = jobRepository;
        this.jobExecutor = jobExecutor;
        this.workerService = workerService;
    }

    @PostConstruct
    public void registerWorker() {
        String hostname;
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            hostname = "unknown";
        }
        workerService.register(workerId, hostname);
    }
    
    @Scheduled(fixedDelay = 500)
    @Transactional
    public void pollAndClaim() {
        if (jobExecutor.isShuttingDown()) {
            return;
        }

        List<Long> claimedIds = jobRepository.claimNextJobIds(workerId, BATCH_SIZE);
        if (!claimedIds.isEmpty()) {
            List<Job> claimedJobs = jobRepository.findByIdIn(claimedIds);
            claimedJobs.forEach(job -> jobExecutor.submit(job, workerId));
        }
    }

    @Scheduled(fixedDelay = 5000)
    public void heartbeat() {
        workerService.heartbeat(workerId, jobExecutor.getActiveJobCount());
    }

    @PreDestroy
    public void onShutdown() {
        workerService.markStopped(workerId);
    }
}
