package com.pharm.pharmavigil_platform.resources.supervisor.dashboard;

import java.util.UUID;

public record DateProductRow(
        UUID machineId,
        String machineName,
        String machineStatus,
        String departmentName,
        UUID staffId,
        String staffName,
        String staffUsername,
        String staffEmail,
        UUID shiftId,
        String shiftName,
        UUID productId,
        String productName,
        String batchNumber,
        long output,
        String unit,
        int stopCount,
        String stopsLink,
        String duration,
        String deviation,
        String hold,
        String consignee,
        String completedStages
) {}
