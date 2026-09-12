package com.pharm.pharmavigil_platform.resources.batch;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public record BatchLogEntryResponse(
        UUID id,
        UUID departmentId,
        String departmentName,
        boolean terminalDepartment,
        UUID machineId,
        String machineName,
        UUID productId,
        String productName,
        LocalDateTime lineClearanceAt,
        boolean isRejected,
        String rejectedReason,
        boolean isCompleted,
        Instant createdAt,
        String createdBy,
        String createdByFullName
) {}
