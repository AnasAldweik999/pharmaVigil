package com.pharm.pharmavigil_platform.resources.workreport;

import com.pharm.pharmavigil_platform.domain.MachineStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record WorkReportResponse(
        UUID id,
        UUID staffId,
        String staffName,
        String staffUsername,
        String staffEmail,
        UUID shiftId,
        String shiftName,
        LocalDate reportDate,
        Instant createdAt,
        Instant updatedAt,
        List<MachineEntryResponse> machines
) {
    public record MachineEntryResponse(
            UUID id,
            UUID machineId,
            String machineName,
            UUID departmentId,
            String departmentName,
            MachineStatus status,
            List<ProductEntryResponse> products
    ) {}

    public record ProductEntryResponse(
            UUID id,
            UUID productId,
            String productName,
            String batchNo,
            long output,
            String unit,
            String consignee,
            Map<String, Boolean> stages,
            List<StopResponse> stops,
            QualityResponse quality
    ) {}

    public record StopResponse(UUID id, UUID stopTypeId, String stopTypeName, String duration, String note) {}

    public record QualityResponse(
            boolean deviation,
            String deviationDetails,
            boolean hold,
            String holdDetails
    ) {}
}
