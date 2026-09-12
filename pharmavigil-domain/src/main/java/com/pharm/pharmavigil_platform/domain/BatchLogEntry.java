package com.pharm.pharmavigil_platform.domain;

import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchLogEntry {

    private UUID id;
    private UUID departmentId;
    private String departmentName;
    private boolean terminalDepartment;
    private UUID machineId;
    private String machineName;
    private UUID productId;
    private String productName;
    private LocalDateTime lineClearanceAt;
    private boolean rejected;
    private String rejectedReason;
    private boolean completed;
    private Instant createdAt;
    private String createdBy;
    private String createdByFullName;
}
