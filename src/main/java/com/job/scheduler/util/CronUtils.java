package com.job.scheduler.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.scheduling.support.CronExpression;


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
    public static Instant nextRun(String cron, Instant from) {
        CronExpression expression = CronExpression.parse(cron);
        ZonedDateTime nextZoned = expression.next(ZonedDateTime.ofInstant(from, ZONE));
        return nextZoned == null ? null : nextZoned.toInstant();
    }
}
