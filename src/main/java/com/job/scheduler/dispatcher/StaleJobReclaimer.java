package com.job.scheduler.dispatcher;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.job.scheduler.entity.Job;
import com.job.scheduler.enums.JobStatus;
import com.job.scheduler.repository.JobRepository;
import com.job.scheduler.service.WorkerService;

@Component
public class StaleJobReclaimer {

    private static final Logger log = LoggerFactory.getLogger(StaleJobReclaimer.class);

    private static final long RUNNING_TIMEOUT_MINUTES = 10;
    private static final long CLAIM_TIMEOUT_SECONDS = 30;
    private static final long WORKER_STALE_SECONDS = 30;

    private final JobRepository jobRepository;
    private final WorkerService workerService;

    public StaleJobReclaimer(JobRepository jobRepository, WorkerService workerService) {
        this.jobRepository = jobRepository;
        this.workerService = workerService;
    }

    @Scheduled(fixedDelay = 15000)
    @Transactional
    public void reclaimStuckJobs() {
        Instant runningThreshold = Instant.now().minus(RUNNING_TIMEOUT_MINUTES, ChronoUnit.MINUTES);
        Instant claimThreshold = Instant.now().minusSeconds(CLAIM_TIMEOUT_SECONDS);

        List<Job> stuck = jobRepository.findStuckJobs(runningThreshold);
        List<Job> staleClaims = jobRepository.findStuckClaims(claimThreshold);

        requeue(stuck, "running longer than " + RUNNING_TIMEOUT_MINUTES + " minutes without completing");
        requeue(staleClaims, "claimed but never started within " + CLAIM_TIMEOUT_SECONDS + " seconds");

        workerService.markStaleWorkersStopped(Instant.now().minusSeconds(WORKER_STALE_SECONDS));
    }

    private void requeue(List<Job> jobs, String reason) {
        for (Job job : jobs) {
            log.warn("Reclaiming job {} (was on worker {}): {}", job.getId(), job.getWorkerId(), reason);
            job.setStatus(JobStatus.QUEUED);
            job.setWorkerId(null);
            job.setClaimedAt(null);
            job.setStartedAt(null);
            job.setLastError("Reclaimed: " + reason);
            jobRepository.save(job);
        }
    }
}
