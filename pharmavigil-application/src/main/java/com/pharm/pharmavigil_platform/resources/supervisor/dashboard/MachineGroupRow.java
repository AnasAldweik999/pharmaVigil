package com.pharm.pharmavigil_platform.resources.supervisor.dashboard;

import java.util.Map;
import java.util.UUID;

public record MachineGroupRow(
        UUID machineId,
        String machineName,
        String departmentName,
        long productCount,
        long stopCount,
        String downtimeMinutes,
        long holdCount,
        long deviationCount,
        Map<String, String> _links
) {}
