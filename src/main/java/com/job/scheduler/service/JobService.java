package com.job.scheduler.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.job.scheduler.dto.BatchJobRequest;
import com.job.scheduler.dto.CreateJobRequest;
import com.job.scheduler.dto.DeadLetterResponse;
import com.job.scheduler.dto.JobExecutionResponse;
import com.job.scheduler.dto.JobLogResponse;
import com.job.scheduler.dto.JobResponse;
import com.job.scheduler.entity.DeadLetterEntry;
import com.job.scheduler.entity.Job;
import com.job.scheduler.entity.JobExecution;
import com.job.scheduler.entity.JobLog;
import com.job.scheduler.entity.Queue;
import com.job.scheduler.enums.JobStatus;
import com.job.scheduler.repository.DeadLetterEntryRepository;
import com.job.scheduler.repository.JobExecutionRepository;
import com.job.scheduler.repository.JobLogRepository;
import com.job.scheduler.repository.JobRepository;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final QueueService queueService;
    private final JobLogRepository jobLogRepository;
    private final JobExecutionRepository jobExecutionRepository;
    private final DeadLetterEntryRepository deadLetterEntryRepository;

    public JobService(JobRepository jobRepository, QueueService queueService,
            JobLogRepository jobLogRepository, JobExecutionRepository jobExecutionRepository,
            DeadLetterEntryRepository deadLetterEntryRepository) {
        this.jobRepository = jobRepository;
        this.queueService = queueService;
        this.jobLogRepository = jobLogRepository;
        this.jobExecutionRepository = jobExecutionRepository;
        this.deadLetterEntryRepository = deadLetterEntryRepository;
    }

    public JobResponse createJob(Long queueId, CreateJobRequest req, Long userId) {
        Queue queue = queueService.getOwnedQueue(userId, queueId);

        if (queue.isPaused()) {
            throw new IllegalStateException("Cannot enqueue job: queue is paused");
        }

        Job job = buildJob(queue, req);
        job = jobRepository.save(job);
        return toResponse(job);
    }

    public List<JobResponse> createBatch(Long queueId, BatchJobRequest req, Long userId) {
        Queue queue = queueService.getOwnedQueue(userId, queueId);
        if (queue.isPaused()) {
            throw new IllegalStateException("Cannot enqueue jobs: queue is paused");
        }

        String batchId = UUID.randomUUID().toString();
        List<Job> jobs = new ArrayList<>();
        for (CreateJobRequest jobReq : req.getJobs()) {
            Job job = buildJob(queue, jobReq);
            job.setBatchId(batchId);
            jobs.add(job);
        }
        jobs = jobRepository.saveAll(jobs);
        return jobs.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public JobResponse getJob(Long queueId, Long jobId, Long userId) {
        queueService.getOwnedQueue(userId, queueId);
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));
        assertBelongsToQueue(job, queueId);
        return toResponse(job);
    }

    public Page<JobResponse> listJobs(Long queueId, JobStatus status, Long userId, Pageable pageable) {
        queueService.getOwnedQueue(userId, queueId);
        Page<Job> page = (status != null)
                ? jobRepository.findByQueueIdAndStatus(queueId, status, pageable)
                : jobRepository.findByQueueId(queueId, pageable);
        return page.map(this::toResponse);
    }

    public JobResponse retryJob(Long queueId, Long jobId, Long userId) {
        queueService.getOwnedQueue(userId, queueId);
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));
        assertBelongsToQueue(job, queueId);

        if (job.getStatus() != JobStatus.DEAD_LETTER && job.getStatus() != JobStatus.FAILED) {
            throw new IllegalStateException("Only FAILED or DEAD_LETTER jobs can be retried manually");
        }

        job.setStatus(JobStatus.QUEUED);
        job.setRetryCount(0);
        job.setLastError(null);
        job.setWorkerId(null);
        job.setClaimedAt(null);
        job.setStartedAt(null);
        job.setCompletedAt(null);
        job = jobRepository.save(job);

        addLog(job, "INFO", "Job manually requeued for retry");
        return toResponse(job);
    }

    public void cancelJob(Long queueId, Long jobId, Long userId) {
        queueService.getOwnedQueue(userId, queueId);
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));
        assertBelongsToQueue(job, queueId);

        if (job.getStatus() != JobStatus.QUEUED && job.getStatus() != JobStatus.SCHEDULED) {
            throw new IllegalStateException("Only QUEUED or SCHEDULED jobs can be cancelled");
        }
        job.setStatus(JobStatus.CANCELED);
        jobRepository.save(job);
        addLog(job, "INFO", "Job cancelled by user");
    }

    public List<JobLogResponse> getLogs(Long queueId, Long jobId, Long userId) {
        queueService.getOwnedQueue(userId, queueId);
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));
        assertBelongsToQueue(job, queueId);

        return jobLogRepository.findByJobIdOrderByCreatedAtAsc(jobId).stream()
                .map(this::toLogResponse)
                .collect(Collectors.toList());
    }

    public List<JobExecutionResponse> getExecutions(Long queueId, Long jobId, Long userId) {
        queueService.getOwnedQueue(userId, queueId);
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));
        assertBelongsToQueue(job, queueId);

        return jobExecutionRepository.findByJobIdOrderByAttemptNumberAsc(jobId).stream()
                .map(this::toExecutionResponse)
                .collect(Collectors.toList());
    }

    public Page<DeadLetterResponse> listDeadLetters(Long userId, Pageable pageable) {
        return deadLetterEntryRepository.findAllByOrderByFailedAtDesc(pageable)
                .map(this::toDeadLetterResponse);
    }

    public JobResponse requeueFromDeadLetter(Long dlqId, Long userId) {
        DeadLetterEntry entry = deadLetterEntryRepository.findById(dlqId)
                .orElseThrow(() -> new IllegalArgumentException("Dead letter entry not found"));
        Job job = entry.getJob();
        if (!job.getQueue().getProject().getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("You do not have access to this job");
        }

        job.setStatus(JobStatus.QUEUED);
        job.setRetryCount(0);
        job.setLastError(null);
        job.setWorkerId(null);
        job.setClaimedAt(null);
        job.setStartedAt(null);
        job.setCompletedAt(null);
        job = jobRepository.save(job);
        addLog(job, "INFO", "Job requeued from dead letter queue");
        return toResponse(job);
    }

    private Job buildJob(Queue queue, CreateJobRequest req) {
        Job job = new Job();
        job.setQueue(queue);
        job.setType(req.getType());
        job.setPayload(req.getPayLoad());
        job.setPriority(req.getPriority() != null ? req.getPriority() : queue.getPriority());
        job.setMaxRetries(req.getMaxRetries() != null ? req.getMaxRetries() : queue.getDefaultMaxRetries());
        job.setRetryStrategy(req.getRetryStrategy() != null ? req.getRetryStrategy() : queue.getDefaultRetryStrategy());

        if (req.getAtTime() != null) {
            job.setStatus(JobStatus.SCHEDULED);
            job.setScheduledAt(Instant.parse(req.getAtTime()));
        } else if (req.getDelaySecond() != null) {
            job.setStatus(JobStatus.SCHEDULED);
            job.setScheduledAt(Instant.now().plusSeconds(req.getDelaySecond()));
        } else if (req.getCronExp() != null) {
            job.setStatus(JobStatus.SCHEDULED);
            job.setCronExpression(req.getCronExp());
            job.setScheduledAt(com.job.scheduler.util.CronUtils.nextRun(req.getCronExp(), Instant.now()));
        } else {
            job.setStatus(JobStatus.QUEUED); // immediate
        }
        return job;
    }

    private void assertBelongsToQueue(Job job, Long queueId) {
        if (!job.getQueue().getId().equals(queueId)) {
            throw new IllegalArgumentException("Job does not belong to this queue");
        }
    }

    private void addLog(Job job, String level, String message) {
        JobLog log = new JobLog();
        log.setJob(job);
        log.setLevel(level);
        log.setMessage(message);
        jobLogRepository.save(log);
    }

    private JobResponse toResponse(Job job) {
        JobResponse resp = new JobResponse();
        resp.setId(job.getId());
        resp.setQueueId(job.getQueue().getId());
        resp.setType(job.getType());
        resp.setPayload(job.getPayload());
        resp.setStatus(job.getStatus());
        resp.setPriority(job.getPriority());
        resp.setRetryCount(job.getRetryCount());
        resp.setMaxRetries(job.getMaxRetries());
        resp.setRetryStrategy(job.getRetryStrategy());
        resp.setWorkerId(job.getWorkerId());
        resp.setCronExpression(job.getCronExpression());
        resp.setBatchId(job.getBatchId());
        resp.setLastError(job.getLastError());
        resp.setScheduledAt(job.getScheduledAt() == null ? null : job.getScheduledAt().toString());
        resp.setClaimedAt(job.getClaimedAt() == null ? null : job.getClaimedAt().toString());
        resp.setStartedAt(job.getStartedAt() == null ? null : job.getStartedAt().toString());
        resp.setCompletedAt(job.getCompletedAt() == null ? null : job.getCompletedAt().toString());
        resp.setCreatedAt(job.getCreatedAt().toString());
        return resp;
    }

    private JobLogResponse toLogResponse(JobLog log) {
        JobLogResponse resp = new JobLogResponse();
        resp.setId(log.getId());
        resp.setLevel(log.getLevel());
        resp.setMessage(log.getMessage());
        resp.setCreatedAt(log.getCreatedAt().toString());
        return resp;
    }

    private JobExecutionResponse toExecutionResponse(JobExecution exec) {
        JobExecutionResponse resp = new JobExecutionResponse();
        resp.setId(exec.getId());
        resp.setAttemptNumber(exec.getAttemptNumber());
        resp.setWorkerId(exec.getWorkerId());
        resp.setStatus(exec.getStatus().name());
        resp.setStartedAt(exec.getStartedAt() == null ? null : exec.getStartedAt().toString());
        resp.setCompletedAt(exec.getCompletedAt() == null ? null : exec.getCompletedAt().toString());
        resp.setErrorMessage(exec.getErrorMessage());
        return resp;
    }

    private DeadLetterResponse toDeadLetterResponse(DeadLetterEntry entry) {
        DeadLetterResponse resp = new DeadLetterResponse();
        resp.setId(entry.getId());
        resp.setJobId(entry.getJob().getId());
        resp.setJobType(entry.getJob().getType());
        resp.setReason(entry.getReason());
        resp.setPayloadSnapshot(entry.getPayloadSnapshot());
        resp.setRetryCountAtFailure(entry.getRetryCountAtFailure());
        resp.setFailedAt(entry.getFailedAt().toString());
        return resp;
    }
}
