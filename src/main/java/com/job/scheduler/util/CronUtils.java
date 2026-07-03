package com.job.scheduler.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.scheduling.support.CronExpression;

/**
 * Thin wrapper around Spring's CronExpression so the rest of the codebase doesn't
 * need to deal with ZonedDateTime <-> Instant conversions directly.
 *
 * Supports standard 6-field Spring cron syntax, e.g. "0 0/5 * * * *" (every 5 minutes).
 */
public final class CronUtils {

    private static final ZoneId ZONE = ZoneId.of("Asia/Kolkata");

    private CronUtils() {
    }

    public static boolean isValid(String cron) {
        if (cron == null || cron.isBlank()) {
            return false;
        }
        try {
            CronExpression.parse(cron);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Returns the next fire time strictly after `from`, or null if the expression
     * has no further matches (shouldn't normally happen for valid cron expressions).
     */
    public static Instant nextRun(String cron, Instant from) {
        CronExpression expression = CronExpression.parse(cron);
        ZonedDateTime nextZoned = expression.next(ZonedDateTime.ofInstant(from, ZONE));
        return nextZoned == null ? null : nextZoned.toInstant();
    }
}
