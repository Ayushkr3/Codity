package com.job.scheduler.dispatcher;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.job.scheduler.entity.DeadLetterEntry;
import com.job.scheduler.entity.Job;
import com.job.scheduler.entity.JobExecution;
import com.job.scheduler.entity.JobLog;
import com.job.scheduler.enums.JobStatus;
import com.job.scheduler.handler.JobHandler;
import com.job.scheduler.repository.DeadLetterEntryRepository;
import com.job.scheduler.repository.JobExecutionRepository;
import com.job.scheduler.repository.JobLogRepository;
import com.job.scheduler.repository.JobRepository;
import com.job.scheduler.util.RetryPolicy;

@Component
public class JobExecutor {

    private static final Logger log = LoggerFactory.getLogger(JobExecutor.class);

    private final ExecutorService threadPool = Executors.newFixedThreadPool(10);
    private final JobRepository jobRepository;
    private final JobExecutionRepository jobExecutionRepository;
    private final JobLogRepository jobLogRepository;
    private final DeadLetterEntryRepository deadLetterEntryRepository;
    private final Map<String, JobHandler> handlers;

    private final AtomicInteger activeJobCount = new AtomicInteger(0);
    private final AtomicBoolean shuttingDown = new AtomicBoolean(false);

    public JobExecutor(JobRepository jobRepository, JobExecutionRepository jobExecutionRepository,
            JobLogRepository jobLogRepository, DeadLetterEntryRepository deadLetterEntryRepository,
            Map<String, JobHandler> handlers) {
        this.jobRepository = jobRepository;
        this.jobExecutionRepository = jobExecutionRepository;
        this.jobLogRepository = jobLogRepository;
        this.deadLetterEntryRepository = deadLetterEntryRepository;
        this.handlers = handlers;
    }

    public int getActiveJobCount() {
        return activeJobCount.get();
    }

    public boolean isShuttingDown() {
        return shuttingDown.get();
    }

    public void submit(Job job, String workerId) {
        if (shuttingDown.get()) {
            return;
        }
        activeJobCount.incrementAndGet();
        threadPool.submit(() -> {
            try {
                execute(job, workerId);
            } finally {
                activeJobCount.decrementAndGet();
            }
        });
    }

    private void execute(Job job, String workerId) {
        int attemptNumber = job.getRetryCount() + 1;
        JobExecution execution = startExecution(job, workerId, attemptNumber);
        markRunning(job, workerId);
        addLog(job, "INFO", "Attempt " + attemptNumber + " started on worker " + workerId);

        JobHandler handler = handlers.get(job.getType());
        if (handler == null) {
            String reason = "No handler registered for type: " + job.getType();
            failPermanently(job, reason);
            finishExecution(execution, JobStatus.FAILED, reason);
            addLog(job, "ERROR", reason);
            return;
        }

        try {
            handler.handle(job.getPayload());
            markCompleted(job);
            finishExecution(execution, JobStatus.COMPLETED, null);
            addLog(job, "INFO", "Attempt " + attemptNumber + " completed successfully");
        } catch (Exception e) {
            handleFailure(job, e);
            finishExecution(execution, JobStatus.FAILED, e.getMessage());
            addLog(job, "ERROR", "Attempt " + attemptNumber + " failed: " + e.getMessage());
        }
    }

    protected void markRunning(Job job, String workerId) {
        job.setStatus(JobStatus.RUNNING);
        job.setWorkerId(workerId);
        job.setStartedAt(Instant.now());
        jobRepository.save(job);
    }

    protected void markCompleted(Job job) {
        job.setStatus(JobStatus.COMPLETED);
        job.setCompletedAt(Instant.now());
        jobRepository.save(job);
    }

    protected void handleFailure(Job job, Exception e) {
        job.setLastError(e.getMessage());
        job.setRetryCount(job.getRetryCount() + 1);

        if (job.getRetryCount() < job.getMaxRetries()) {
            long delaySeconds = RetryPolicy.computeBackoffSeconds(job.getRetryStrategy(), job.getRetryCount());
            job.setStatus(JobStatus.SCHEDULED);
            job.setScheduledAt(Instant.now().plusSeconds(delaySeconds));
            jobRepository.save(job);
        } else {
            job.setStatus(JobStatus.DEAD_LETTER);
            jobRepository.save(job);
            snapshotToDeadLetter(job, "Exceeded max retries (" + job.getMaxRetries() + "): " + e.getMessage());
        }
    }

    protected void failPermanently(Job job, String reason) {
        job.setLastError(reason);
        job.setStatus(JobStatus.DEAD_LETTER);
        jobRepository.save(job);
        snapshotToDeadLetter(job, reason);
    }

    private void snapshotToDeadLetter(Job job, String reason) {
        DeadLetterEntry entry = new DeadLetterEntry();
        entry.setJob(job);
        entry.setReason(reason);
        entry.setPayloadSnapshot(job.getPayload());
        entry.setRetryCountAtFailure(job.getRetryCount());
        deadLetterEntryRepository.save(entry);
    }

    private JobExecution startExecution(Job job, String workerId, int attemptNumber) {
        JobExecution execution = new JobExecution();
        execution.setJob(job);
        execution.setAttemptNumber(attemptNumber);
        execution.setWorkerId(workerId);
        execution.setStatus(JobStatus.RUNNING);
        execution.setStartedAt(Instant.now());
        return jobExecutionRepository.save(execution);
    }

    private void finishExecution(JobExecution execution, JobStatus status, String errorMessage) {
        execution.setStatus(status);
        execution.setCompletedAt(Instant.now());
        execution.setErrorMessage(errorMessage);
        jobExecutionRepository.save(execution);
    }

    private void addLog(Job job, String level, String message) {
        JobLog jobLog = new JobLog();
        jobLog.setJob(job);
        jobLog.setLevel(level);
        jobLog.setMessage(message);
        jobLogRepository.save(jobLog);
    }

    @PreDestroy
    public void shutdown() {
        shuttingDown.set(true);
        log.info("JobExecutor draining: waiting for {} active job(s) to finish", activeJobCount.get());
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(30, TimeUnit.SECONDS)) {
                log.warn("JobExecutor did not drain within 30s, forcing shutdown; in-flight jobs will be " +
                        "reclaimed by the next worker via the stale-claim reclaimer");
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
