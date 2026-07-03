package com.job.scheduler.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.job.scheduler.entity.RecurringJobDefinition;

public interface RecurringJobDefinitionRepository extends JpaRepository<RecurringJobDefinition, Long> {

    List<RecurringJobDefinition> findByEnabledTrueAndNextRunAtLessThanEqual(Instant now);

    List<RecurringJobDefinition> findByQueueId(Long queueId);
}
