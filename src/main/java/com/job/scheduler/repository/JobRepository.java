package com.job.scheduler.repository;

import org.springframework.stereotype.Repository;

import jakarta.persistence.*;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

import javax.print.attribute.standard.JobState;

import com.job.scheduler.entity.Job;
import com.job.scheduler.enums.JobStatus;

public interface JobRepository extends JpaRepository<Job, Long> {
    Page<Job> findByQueueIdAndStatus(Long queueId, JobStatus status, Pageable pageable);

    Page<Job> findByQueueId(Long queueId, Pageable pageable);
    @Modifying
    @Query("""
        UPDATE Job j SET j.status = 'QUEUED'
        WHERE j.status = 'SCHEDULED' AND j.scheduledAt <= :now
        """)
    int promoteScheduledJobsToQueued(@Param("now") Instant now);
    @Modifying
    @Query(value = """
        UPDATE jobs
        SET status = 'CLAIMED', worker_id = :workerId, claimed_at = now()
        WHERE id IN (
            SELECT id FROM jobs
            WHERE status = 'QUEUED'
            ORDER BY priority DESC, scheduled_at ASC NULLS FIRST
            FOR UPDATE SKIP LOCKED
            LIMIT :batchSize
        )
        RETURNING id
        """, nativeQuery = true)
    List<Long> claimNextJobIds(@Param("workerId") String workerId,
                                @Param("batchSize") int batchSize);
    List<Job> findByIdIn(List<Long> ids);
    @Query("""
        SELECT j FROM Job j
        WHERE j.status = 'RUNNING' AND j.startedAt < :threshold
        """)
    List<Job> findStuckJobs(@Param("threshold") Instant threshold);
}
