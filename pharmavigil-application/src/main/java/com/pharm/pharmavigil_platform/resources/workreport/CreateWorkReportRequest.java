package com.pharm.pharmavigil_platform.resources.workreport;

import com.pharm.pharmavigil_platform.domain.MachineStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record CreateWorkReportRequest(
        LocalDate reportDate,
        UUID shiftId,
        List<MachineEntryRequest> machines
) {

    public record MachineEntryRequest(
            UUID machineId,
            UUID departmentId,
            MachineStatus status,
            List<ProductEntryRequest> products
    ) {}

    public record ProductEntryRequest(
            UUID productId,
            String batchNo,
            long output,
            String unit,
            String consignee,
            Map<String, Boolean> stages,
            List<StopRequest> stops,
            QualityRequest quality
    ) {}

    public record StopRequest(
            UUID stopTypeId,
            String duration,
            String note
    ) {}

    public record QualityRequest(
            boolean deviation,
            String deviationDetails,
            boolean hold,
            String holdDetails
    ) {}
}
