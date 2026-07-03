package com.job.scheduler.service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.job.scheduler.dto.RecurringJobRequest;
import com.job.scheduler.dto.RecurringJobResponse;
import com.job.scheduler.entity.Queue;
import com.job.scheduler.entity.RecurringJobDefinition;
import com.job.scheduler.repository.RecurringJobDefinitionRepository;
import com.job.scheduler.util.CronUtils;
@Service
public class RecurringJobService {

    private final RecurringJobDefinitionRepository repository;
    private final QueueService queueService;

    public RecurringJobService(RecurringJobDefinitionRepository repository, QueueService queueService) {
        this.repository = repository;
        this.queueService = queueService;
    }

    public RecurringJobResponse create(Long userId, Long queueId, RecurringJobRequest req) {
        Queue queue = queueService.getOwnedQueue(userId, queueId);

        if (!CronUtils.isValid(req.getCronExpression())) {
            throw new IllegalArgumentException("Invalid cron expression: " + req.getCronExpression());
        }

        RecurringJobDefinition def = new RecurringJobDefinition();
        def.setQueue(queue);
        def.setName(req.getName());
        def.setType(req.getType());
        def.setPayload(req.getPayload());
        def.setCronExpression(req.getCronExpression());
        if (req.getPriority() != null) {
            def.setPriority(req.getPriority());
        }
        if (req.getMaxRetries() != null) {
            def.setMaxRetries(req.getMaxRetries());
        }
        if (req.getRetryStrategy() != null) {
            def.setRetryStrategy(req.getRetryStrategy());
        }
        def.setEnabled(true);
        def.setNextRunAt(CronUtils.nextRun(req.getCronExpression(), Instant.now()));

        def = repository.save(def);
        return toResponse(def);
    }

    public List<RecurringJobResponse> listForQueue(Long userId, Long queueId) {
        queueService.getOwnedQueue(userId, queueId);
        return repository.findByQueueId(queueId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    public Boolean deleteRecurringJob(Long userId, Long queueId,String name){
        queueService.getOwnedQueue(userId, queueId);
        RecurringJobDefinition job = repository.findByName(name).orElse(null);
        if(job!=null){
            repository.delete(job);
            return true;
        }
        return false;
    }
    public RecurringJobResponse setEnabled(Long userId, Long id, boolean enabled) {
        RecurringJobDefinition def = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recurring job not found"));
        queueService.getOwnedQueue(userId, def.getQueue().getId()); // ownership check
        def.setEnabled(enabled);
        if (enabled && (def.getNextRunAt() == null || def.getNextRunAt().isBefore(Instant.now()))) {
            def.setNextRunAt(CronUtils.nextRun(def.getCronExpression(), Instant.now()));
        }
        def = repository.save(def);
        return toResponse(def);
    }

    private RecurringJobResponse toResponse(RecurringJobDefinition def) {
        RecurringJobResponse resp = new RecurringJobResponse();
        resp.setId(def.getId());
        resp.setQueueId(def.getQueue().getId());
        resp.setName(def.getName());
        resp.setType(def.getType());
        resp.setCronExpression(def.getCronExpression());
        resp.setEnabled(def.getEnabled());
        resp.setNextRunAt(def.getNextRunAt() == null ? null : def.getNextRunAt().toString());
        resp.setLastRunAt(def.getLastRunAt() == null ? null : def.getLastRunAt().toString());
        return resp;
    }
}
