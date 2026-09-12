package com.pharm.pharmavigil_platform.resources.supervisor.dashboard;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

public record StopMachineRow(
        UUID machineId,
        String machineName,
        String machineStatus,
        String departmentName,
        UUID staffId,
        String staffName,
        String staffUsername,
        String staffEmail,
        UUID shiftId,
        String shift,
        LocalDate workingDate,
        long totalProducts,
        String totalDowntimeMinutes,
        Map<String, String> _links
) {}
