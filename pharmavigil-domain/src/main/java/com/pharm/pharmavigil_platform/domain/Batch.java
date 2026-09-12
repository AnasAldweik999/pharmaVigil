package com.pharm.pharmavigil_platform.domain;

import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Batch {

    private UUID id;
    private String batchNo;
    private UUID productId;
    private String productName;
    private BatchStatus status;
    private UUID currentDepartmentId;
    private String currentDepartmentName;
    private UUID currentMachineId;
    private String currentMachineName;
    private LocalDateTime firstLoggedAt;
    private LocalDateTime currentDepartmentEnteredAt;
    private boolean generalHoldingAlerted;
    private boolean generalHoldingExceeded;
    private boolean departmentHoldingAlerted;
    private boolean departmentHoldingExceeded;
    @Builder.Default
    private List<BatchLogEntry> entries = new ArrayList<>();
    private Instant createdAt;
    private String createdBy;
    private Instant lastUpdatedAt;
    private String lastUpdatedBy;
    private String createdByFullName;
    private String lastUpdatedByFullName;

    // Transient, populated only at read time by EnrichBatchHoldingInfoUseCase — not persisted.
    private int effectiveHoldingTimeDays;
    private boolean usingExceptionalHoldingTime;
}
