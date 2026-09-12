package com.pharm.pharmavigil_platform.resources.supervisor.dashboard;

import java.time.LocalDate;
import java.util.UUID;

public record ShiftProductRow(
        UUID machineId,
        String machineName,
        String machineStatus,
        String departmentName,
        UUID staffId,
        String staffName,
        String staffUsername,
        String staffEmail,
        LocalDate workingDate,
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
