package com.pharm.pharmavigil_platform.domain;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkReportMachine {

    private UUID id;
    private WorkReport workReport;
    private UUID machineId;
    private String machineName;
    private UUID departmentId;
    private String departmentName;
    private MachineStatus status;
    @Builder.Default
    private List<WorkReportProduct> products = new ArrayList<>();
}
