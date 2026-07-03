package com.job.scheduler.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;

import org.junit.jupiter.api.Test;

class CronUtilsTest {

    @Test
    void validExpressionIsAccepted() {
        assertTrue(CronUtils.isValid("0 0/5 * * * *")); // every 5 minutes
    }

    @Test
    void blankOrGarbageExpressionIsRejected() {
        assertFalse(CronUtils.isValid(null));
        assertFalse(CronUtils.isValid(""));
        assertFalse(CronUtils.isValid("not a cron expression"));
    }

    @Test
    void nextRunIsStrictlyAfterFrom() {
        Instant now = Instant.now();
        Instant next = CronUtils.nextRun("0 0/5 * * * *", now);
        assertNotNull(next);
        assertTrue(next.isAfter(now));
    }
}
