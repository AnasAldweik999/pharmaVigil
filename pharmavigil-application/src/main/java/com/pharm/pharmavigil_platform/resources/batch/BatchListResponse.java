package com.pharm.pharmavigil_platform.resources.batch;

import com.pharm.pharmavigil_platform.domain.BatchStatus;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public record BatchListResponse(
        UUID id,
        String batchNo,
        UUID productId,
        String productName,
        BatchStatus status,
        UUID currentDepartmentId,
        String currentDepartmentName,
        UUID currentMachineId,
        String currentMachineName,
        LocalDateTime firstLoggedAt,
        long daysSinceFirstLog,
        LocalDateTime currentDepartmentEnteredAt,
        long daysInCurrentDepartment,
        int effectiveDepartmentHoldingTimeDays,
        boolean usingExceptionalHoldingTime,
        boolean generalHoldingAlerted,
        boolean generalHoldingExceeded,
        boolean departmentHoldingAlerted,
        boolean departmentHoldingExceeded,
        Instant createdAt,
        String createdBy,
        Instant lastUpdatedAt,
        String lastUpdatedBy
) {}
