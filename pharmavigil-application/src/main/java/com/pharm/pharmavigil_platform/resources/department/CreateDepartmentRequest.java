package com.pharm.pharmavigil_platform.resources.department;

import java.util.List;
import java.util.UUID;

public record CreateDepartmentRequest(
        String name,
        List<String> stages,
        boolean hasOutputs,
        List<String> units,
        boolean showConsignee,
        boolean terminalDepartment,
        List<UUID> machineIds,
        List<UUID> supervisorIds,
        int standardHoldingTime,
        List<ExceptionalHoldingRequest> exceptionalHoldings
) {}
