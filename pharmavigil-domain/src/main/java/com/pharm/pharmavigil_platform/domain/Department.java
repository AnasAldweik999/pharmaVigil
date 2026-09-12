package com.pharm.pharmavigil_platform.domain;

import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Department {

    private UUID id;
    private String name;
    private boolean active;
    private List<String> stages;
    private boolean hasOutputs;
    private List<String> units;
    private boolean showConsignee;
    private boolean terminalDepartment;
    private List<UUID> machineIds;
    private List<UUID> supervisorIds;
    private int standardHoldingTime;
    private List<ExceptionalHolding> exceptionalHoldings;
    private Instant createdAt;
    private String createdBy;
    private Instant lastUpdatedAt;
    private String lastUpdatedBy;
}
