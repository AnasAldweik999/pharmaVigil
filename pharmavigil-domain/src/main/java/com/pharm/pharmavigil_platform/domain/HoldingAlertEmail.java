package com.pharm.pharmavigil_platform.domain;

/**
 * Payload for one holding-time alert/exceed email to one department supervisor.
 * ruleType: "GENERAL" | "DEPARTMENT". severity: "ALERTED" | "EXCEEDED".
 */
public record HoldingAlertEmail(
        String recipientEmail,
        String recipientName,
        String batchNo,
        String productName,
        String departmentName,
        String ruleType,
        String severity,
        long daysElapsed,
        int thresholdDays,
        boolean exceptionalThreshold
) {}
