package com.pharm.pharmavigil_platform.resources.batch;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateBatchLogEntryRequest(
        UUID productId,
        UUID departmentId,
        UUID machineId,
        LocalDateTime lineClearanceAt,
        boolean completed
) {}
