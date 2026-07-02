package com.job.scheduler.service;

import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Queue;

import org.springframework.stereotype.Service;

import com.job.scheduler.dto.CreateJobRequest;
import com.job.scheduler.dto.JobResponse;
import com.job.scheduler.entity.Job;
import com.job.scheduler.enums.JobStatus;
import com.job.scheduler.repository.JobRepository;

@Service
public class JobService {
    private final JobRepository job_repo;

    public JobService(JobRepository job_repo) {
        this.job_repo = job_repo;
    }

    public JobResponse getJob(Long queue,Long id){
        JobResponse resp = new JobResponse();
        return resp;
    }
    public JobResponse listJobs(){
        JobResponse resp = new JobResponse();
        return resp;
    }
 public JobResponse createJob(Long queueId, CreateJobRequest req) {

    // Queue queue = queueRepository.findById(queueId)
    //         .orElseThrow(() -> new NoSuchElementException("Queue not found"));

    // if (queue.isPaused()) {
    //     throw new IllegalStateException("Cannot enqueue job: queue is paused");
    // }

    Job job = new Job();
    //job.setQueue(queue);
    job.setType(req.getType());
    job.setPayload(req.getPayLoad());
    job.setPriority((req.getPriority() != null) ? req.getPriority() : 0);

    if (req.getAtTime() != null) {
        job.setStatus(JobStatus.SCHEDULED);
        job.setScheduledAt(Instant.parse(req.getAtTime()));
    } else if (req.getDelaySecond() != null) {
        job.setStatus(JobStatus.SCHEDULED);
        job.setScheduledAt(Instant.now().plusSeconds(req.getDelaySecond()));
    } else if (req.getCronExp() != null) {
        job.setStatus(JobStatus.SCHEDULED);
        job.setCronExpression(req.getCronExp());
    } else {
        job.setStatus(JobStatus.QUEUED); // immediate
    }

    job = job_repo.save(job);

    return toResponse(job);
}

private JobResponse toResponse(Job job) {
    JobResponse resp = new JobResponse();
    resp.setId(job.getId());
    resp.setType(job.getType());
    resp.setStatus(job.getStatus());
    resp.setCreatedAt(job.getCreatedAt().toString());
    return resp;
}
}
