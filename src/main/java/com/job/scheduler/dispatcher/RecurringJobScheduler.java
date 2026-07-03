package com.job.scheduler.dispatcher;

import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.job.scheduler.entity.Job;
import com.job.scheduler.entity.RecurringJobDefinition;
import com.job.scheduler.enums.JobStatus;
import com.job.scheduler.repository.JobRepository;
import com.job.scheduler.repository.RecurringJobDefinitionRepository;
import com.job.scheduler.util.CronUtils;


@Component
public class RecurringJobScheduler {

    private static final Logger log = LoggerFactory.getLogger(RecurringJobScheduler.class);

    private final RecurringJobDefinitionRepository recurringJobRepository;
    private final JobRepository jobRepository;

    public RecurringJobScheduler(RecurringJobDefinitionRepository recurringJobRepository,
            JobRepository jobRepository) {
        this.recurringJobRepository = recurringJobRepository;
        this.jobRepository = jobRepository;
    }

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void spawnDueJobs() {
        Instant now = Instant.now();
        List<RecurringJobDefinition> due = recurringJobRepository.findByEnabledTrueAndNextRunAtLessThanEqual(now);

        for (RecurringJobDefinition def : due) {
            if (def.getQueue().isPaused()) {
                advance(def, now);
                continue;
            }

            Job job = new Job();
            job.setQueue(def.getQueue());
            job.setType(def.getType());
            job.setPayload(def.getPayload());
            job.setPriority(def.getPriority());
            job.setMaxRetries(def.getMaxRetries());
            job.setRetryStrategy(def.getRetryStrategy());
            job.setCronExpression(def.getCronExpression());
            job.setStatus(JobStatus.QUEUED);
            jobRepository.save(job);

            def.setLastRunAt(now);
            log.info("Spawned job for recurring definition {} ({})", def.getId(), def.getName());
            advance(def, now);
        }
    }

    private void advance(RecurringJobDefinition def, Instant from) {
        Instant next = CronUtils.nextRun(def.getCronExpression(), from);
        def.setNextRunAt(next);
        recurringJobRepository.save(def);
    }
}
