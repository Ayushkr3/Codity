package com.job.scheduler.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.job.scheduler.enums.RetryMethods;

class RetryPolicyTest {

    @Test
    void fixedStrategyAlwaysReturnsBaseDelay() {
        assertEquals(10, RetryPolicy.computeBackoffSeconds(RetryMethods.FIXED, 1, 10));
        assertEquals(10, RetryPolicy.computeBackoffSeconds(RetryMethods.FIXED, 5, 10));
        assertEquals(10, RetryPolicy.computeBackoffSeconds(RetryMethods.FIXED, 20, 10));
    }

    @Test
    void linearStrategyScalesWithAttemptNumber() {
        assertEquals(10, RetryPolicy.computeBackoffSeconds(RetryMethods.LINEAR, 1, 10));
        assertEquals(20, RetryPolicy.computeBackoffSeconds(RetryMethods.LINEAR, 2, 10));
        assertEquals(50, RetryPolicy.computeBackoffSeconds(RetryMethods.LINEAR, 5, 10));
    }

    @Test
    void exponentialStrategyDoublesEachAttempt() {
        assertEquals(5, RetryPolicy.computeBackoffSeconds(RetryMethods.EXPONENTIAL, 1, 5));
        assertEquals(10, RetryPolicy.computeBackoffSeconds(RetryMethods.EXPONENTIAL, 2, 5));
        assertEquals(20, RetryPolicy.computeBackoffSeconds(RetryMethods.EXPONENTIAL, 3, 5));
        assertEquals(40, RetryPolicy.computeBackoffSeconds(RetryMethods.EXPONENTIAL, 4, 5));
    }

    @Test
    void delayIsCappedAtOneHour() {
        long delay = RetryPolicy.computeBackoffSeconds(RetryMethods.EXPONENTIAL, 30, 5);
        assertTrue(delay <= 3600);
    }

    @Test
    void attemptNumberBelowOneIsTreatedAsOne() {
        assertEquals(
                RetryPolicy.computeBackoffSeconds(RetryMethods.EXPONENTIAL, 1, 5),
                RetryPolicy.computeBackoffSeconds(RetryMethods.EXPONENTIAL, 0, 5));
    }

    @Test
    void nullStrategyDefaultsToExponential() {
        assertEquals(
                RetryPolicy.computeBackoffSeconds(RetryMethods.EXPONENTIAL, 3, 5),
                RetryPolicy.computeBackoffSeconds(null, 3, 5));
    }
}
