package com.job.scheduler.util;

import com.job.scheduler.enums.RetryMethods;

public final class RetryPolicy {

    private static final long DEFAULT_BASE_DELAY_SECONDS = 5;
    private static final long MAX_DELAY_SECONDS = 3600; // 1 hour cap so backoff can't run away

    private RetryPolicy() {
    }

    public static long computeBackoffSeconds(RetryMethods strategy, int attemptNumber) {
        return computeBackoffSeconds(strategy, attemptNumber, DEFAULT_BASE_DELAY_SECONDS);
    }

    public static long computeBackoffSeconds(RetryMethods strategy, int attemptNumber, long baseDelaySeconds) {
        if (attemptNumber < 1) {
            attemptNumber = 1;
        }
        if (strategy == null) {
            strategy = RetryMethods.EXPONENTIAL;
        }

        long delay;
        switch (strategy) {
            case FIXED:
                delay = baseDelaySeconds;
                break;
            case LINEAR:
                delay = baseDelaySeconds * attemptNumber;
                break;
            case EXPONENTIAL:
            default:
                delay = baseDelaySeconds * (1L << Math.min(attemptNumber - 1, 20));
                break;
        }
        return Math.min(delay, MAX_DELAY_SECONDS);
    }
}
