package com.job.scheduler.dispatcher;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Component;

import com.job.scheduler.entity.Job;
import com.job.scheduler.enums.JobStatus;
import com.job.scheduler.handler.JobHandler;
import com.job.scheduler.repository.JobRepository;

@Component
public class JobExecutor {

    private final ExecutorService threadPool = Executors.newFixedThreadPool(10);
    private final JobRepository jobRepository;
    private final Map<String, JobHandler> handlers;

    public JobExecutor(JobRepository jobRepository, Map<String, JobHandler> handlers) {
        this.jobRepository = jobRepository;
        this.handlers = handlers;
    }

    public void submit(Job job) {
        threadPool.submit(() -> execute(job));
    }

    private void execute(Job job) {
        markRunning(job);

        JobHandler handler = handlers.get(job.getType());
        if (handler == null) {
            failPermanently(job, "No handler registered for type: " + job.getType());
            return;
        }

        try {
            handler.handle(job.getPayload());
            markCompleted(job);
        } catch (Exception e) {
            handleFailure(job, e);
        }
    }

    private void markRunning(Job job) {
        job.setStatus(JobStatus.RUNNING);
        job.setStartedAt(Instant.now());
        jobRepository.save(job);
    }

    private void markCompleted(Job job) {
        job.setStatus(JobStatus.COMPLETED);
        job.setCompletedAt(Instant.now());
        jobRepository.save(job);
    }

    private void handleFailure(Job job, Exception e) {
        job.setLastError(e.getMessage());
        job.setRetryCount(job.getRetryCount() + 1);

        if (job.getRetryCount() < job.getMaxRetries()) {
            long delaySeconds = computeBackoff(job.getRetryCount());
            job.setStatus(JobStatus.SCHEDULED);
            job.setScheduledAt(Instant.now().plusSeconds(delaySeconds));
        } else {
            job.setStatus(JobStatus.DEAD_LETTER);
        }
        jobRepository.save(job);
    }

    private void failPermanently(Job job, String reason) {
        job.setLastError(reason);
        job.setStatus(JobStatus.DEAD_LETTER);
        jobRepository.save(job);
    }

    private long computeBackoff(int retryCount) {
        return (long) Math.pow(2, retryCount) * 5;
    }
}