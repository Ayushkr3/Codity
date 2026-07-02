package com.job.scheduler.service;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.job.scheduler.dto.CreateJobRequest;
import com.job.scheduler.dto.JobResponse;
import com.job.scheduler.entity.Job;
import com.job.scheduler.entity.Project;
import com.job.scheduler.entity.Queue;
import com.job.scheduler.entity.User;
import com.job.scheduler.enums.JobStatus;
import com.job.scheduler.repository.JobRepository;
import com.job.scheduler.repository.ProjectRepository;
import com.job.scheduler.repository.QueueRepository;

@Service
public class JobService {
    private final JobRepository job_repo;
    private final QueueRepository q_repo;
    private final ProjectRepository pr_repo;
    public JobService(JobRepository job_repo,QueueRepository q_repo,ProjectRepository pr_repo) {
        this.job_repo = job_repo;
        this.q_repo = q_repo;
        this.pr_repo = pr_repo;
    }

    public JobResponse getJob(Long queue,Long id){
        JobResponse resp = new JobResponse();
        return resp;
    }
    public JobResponse listJobs(){
        JobResponse resp = new JobResponse();
        return resp;
    }
 public JobResponse createJob(Long queueId, CreateJobRequest req,Long user) {
    Project px = pr_repo.findByOwnerId(user).orElse(null);// (user).orElse(null);
    if(px==null){
        throw new IllegalStateException("Project not found");
    }
    Queue queue = q_repo.findById(queueId).orElse(null);
    if(queue==null){
        queue = q_repo.findById(0l).orElse(null);
        if(queue==null){
            Queue x = new Queue();
            x.setName("Default");
            x.setProject(px);
            q_repo.save(x);
            queue = x;
        }
    }

    if (queue.isPaused()) {
        throw new IllegalStateException("Cannot enqueue job: queue is paused");
    }
    Job job = new Job();
    //q.setName(name);
    //queue.setId(queueId);
    job.setQueue(queue);
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
