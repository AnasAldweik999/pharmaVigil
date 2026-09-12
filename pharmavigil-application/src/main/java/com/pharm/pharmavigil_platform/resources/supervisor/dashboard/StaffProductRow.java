package com.pharm.pharmavigil_platform.resources.supervisor.dashboard;

import java.time.LocalDate;
import java.util.UUID;

public record StaffProductRow(
        UUID machineId,
        String machineName,
        String machineStatus,
        String departmentName,
        LocalDate workingDate,
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
