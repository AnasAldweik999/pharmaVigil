package com.pharm.pharmavigil_platform.resources.batch;

import com.pharm.pharmavigil_platform.domain.BatchStatus;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record BatchDetailResponse(
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
        List<BatchLogEntryResponse> entries,
        Instant createdAt,
        String createdBy,
        String createdByFullName,
        Instant lastUpdatedAt,
        String lastUpdatedBy,
        String lastUpdatedByFullName
) {}
