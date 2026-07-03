package com.job.scheduler.service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.job.scheduler.dto.WorkerResponse;
import com.job.scheduler.entity.Worker;
import com.job.scheduler.entity.WorkerHeartbeat;
import com.job.scheduler.enums.WorkerStatus;
import com.job.scheduler.repository.WorkerHeartbeatRepository;
import com.job.scheduler.repository.WorkerRepository;

@Service
public class WorkerService {

    private final WorkerRepository workerRepository;
    private final WorkerHeartbeatRepository heartbeatRepository;

    public WorkerService(WorkerRepository workerRepository, WorkerHeartbeatRepository heartbeatRepository) {
        this.workerRepository = workerRepository;
        this.heartbeatRepository = heartbeatRepository;
    }

    public Worker register(String workerId, String hostname) {
        Worker worker = workerRepository.findById(workerId).orElseGet(Worker::new);
        worker.setWorkerId(workerId);
        worker.setHostname(hostname);
        worker.setStatus(WorkerStatus.ACTIVE);
        worker.setStartedAt(worker.getStartedAt() == null ? Instant.now() : worker.getStartedAt());
        worker.setLastHeartbeatAt(Instant.now());
        return workerRepository.save(worker);
    }

    public void heartbeat(String workerId, int activeJobCount) {
        Worker worker = workerRepository.findById(workerId).orElseGet(() -> {
            Worker w = new Worker();
            w.setWorkerId(workerId);
            return w;
        });
        worker.setStatus(WorkerStatus.ACTIVE);
        worker.setActiveJobCount(activeJobCount);
        worker.setLastHeartbeatAt(Instant.now());
        workerRepository.save(worker);

        WorkerHeartbeat hb = new WorkerHeartbeat();
        hb.setWorkerId(workerId);
        hb.setActiveJobCount(activeJobCount);
        hb.setHeartbeatAt(Instant.now());
        heartbeatRepository.save(hb);
    }

    public void markStopped(String workerId) {
        workerRepository.findById(workerId).ifPresent(worker -> {
            worker.setStatus(WorkerStatus.STOPPED);
            worker.setActiveJobCount(0);
            workerRepository.save(worker);
        });
    }
    public List<Worker> markStaleWorkersStopped(Instant threshold) {
        List<Worker> stale = workerRepository.findByLastHeartbeatAtBeforeAndStatusNot(threshold, WorkerStatus.STOPPED);
        stale.forEach(w -> w.setStatus(WorkerStatus.STOPPED));
        return workerRepository.saveAll(stale);
    }

    public List<WorkerResponse> listAll() {
        return workerRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private WorkerResponse toResponse(Worker worker) {
        WorkerResponse resp = new WorkerResponse();
        resp.setWorkerId(worker.getWorkerId());
        resp.setHostname(worker.getHostname());
        resp.setStatus(worker.getStatus());
        resp.setActiveJobCount(worker.getActiveJobCount());
        resp.setStartedAt(worker.getStartedAt().toString());
        resp.setLastHeartbeatAt(worker.getLastHeartbeatAt() == null ? null : worker.getLastHeartbeatAt().toString());
        return resp;
    }
}
