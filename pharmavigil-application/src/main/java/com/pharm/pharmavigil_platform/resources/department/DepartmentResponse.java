package com.pharm.pharmavigil_platform.resources.department;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record DepartmentResponse(
        UUID id,
        String name,
        boolean active,
        List<String> stages,
        boolean hasOutputs,
        List<String> units,
        boolean showConsignee,
        boolean terminalDepartment,
        List<UUID> machineIds,
        List<UUID> supervisorIds,
        int standardHoldingTime,
        List<ExceptionalHoldingResponse> exceptionalHoldings,
        Instant createdAt,
        String createdBy,
        Instant lastUpdatedAt,
        String lastUpdatedBy
) {}
