package com.job.scheduler.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.job.scheduler.dto.QueueRequest;
import com.job.scheduler.dto.QueueResponse;
import com.job.scheduler.entity.Project;
import com.job.scheduler.entity.Queue;
import com.job.scheduler.enums.JobStatus;
import com.job.scheduler.repository.JobRepository;
import com.job.scheduler.repository.ProjectRepository;
import com.job.scheduler.repository.QueueRepository;

@Service
public class QueueService {

    private final QueueRepository queueRepository;
    private final ProjectRepository projectRepository;
    private final JobRepository jobRepository;

    public QueueService(QueueRepository queueRepository, ProjectRepository projectRepository,
            JobRepository jobRepository) {
        this.queueRepository = queueRepository;
        this.projectRepository = projectRepository;
        this.jobRepository = jobRepository;
    }

    public QueueResponse create(Long userId, Long projectId, QueueRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
        assertOwnership(project, userId);

        Queue queue = new Queue();
        queue.setProject(project);
        applyRequest(queue, request);
        queue = queueRepository.save(queue);
        return toResponse(queue, true);
    }

    public List<QueueResponse> listForProject(Long userId, Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
        assertOwnership(project, userId);

        return queueRepository.findByProjectId(projectId).stream()
                .map(q -> toResponse(q, false))
                .collect(Collectors.toList());
    }

    public QueueResponse get(Long userId, Long queueId) {
        Queue queue = getOwnedQueue(userId, queueId);
        return toResponse(queue, true);
    }

    public QueueResponse update(Long userId, Long queueId, QueueRequest request) {
        Queue queue = getOwnedQueue(userId, queueId);
        applyRequest(queue, request);
        queue = queueRepository.save(queue);
        return toResponse(queue, true);
    }

    public QueueResponse setPaused(Long userId, Long queueId, boolean paused) {
        Queue queue = getOwnedQueue(userId, queueId);
        queue.setPaused(paused);
        queue = queueRepository.save(queue);
        return toResponse(queue, true);
    }

    public Queue getOwnedQueue(Long userId, Long queueId) {
        Queue queue = queueRepository.findById(queueId)
                .orElseThrow(() -> new IllegalArgumentException("Queue not found"));
        assertOwnership(queue.getProject(), userId);
        return queue;
    }

    private void applyRequest(Queue queue, QueueRequest request) {
        if (request.getName() != null) {
            queue.setName(request.getName());
        }
        if (request.getPriority() != null) {
            queue.setPriority(request.getPriority());
        }
        if (request.getConcurrencyLimit() != null) {
            queue.setConcurrencyLimit(request.getConcurrencyLimit());
        }
        if (request.getDefaultMaxRetries() != null) {
            queue.setDefaultMaxRetries(request.getDefaultMaxRetries());
        }
        if (request.getDefaultRetryStrategy() != null) {
            queue.setDefaultRetryStrategy(request.getDefaultRetryStrategy());
        }
    }

    private void assertOwnership(Project project, Long userId) {
        if (!project.getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("You do not have access to this project");
        }
    }

    private QueueResponse toResponse(Queue queue, boolean includeStats) {
        QueueResponse resp = new QueueResponse();
        resp.setId(queue.getId());
        resp.setProjectId(queue.getProject().getId());
        resp.setName(queue.getName());
        resp.setPriority(queue.getPriority());
        resp.setConcurrencyLimit(queue.getConcurrencyLimit());
        resp.setPaused(queue.getPaused());
        resp.setDefaultMaxRetries(queue.getDefaultMaxRetries());
        resp.setDefaultRetryStrategy(queue.getDefaultRetryStrategy());

        if (includeStats) {
            Map<String, Long> counts = new HashMap<>();
            for (JobStatus status : JobStatus.values()) {
                counts.put(status.name(), 0L);
            }
            for (Object[] row : jobRepository.countByStatusForQueue(queue.getId())) {
                JobStatus status = (JobStatus) row[0];
                Long total = (Long) row[1];
                counts.put(status.name(), total);
            }
            resp.setJobCountsByStatus(counts);
        }

        return resp;
    }
}
