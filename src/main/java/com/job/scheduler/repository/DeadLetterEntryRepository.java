package com.job.scheduler.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.job.scheduler.entity.DeadLetterEntry;

public interface DeadLetterEntryRepository extends JpaRepository<DeadLetterEntry, Long> {

    Page<DeadLetterEntry> findByJob_Queue_IdOrderByFailedAtDesc(Long queueId, Pageable pageable);

    Optional<DeadLetterEntry> findByJobId(Long jobId);

    Page<DeadLetterEntry> findAllByOrderByFailedAtDesc(Pageable pageable);
}
