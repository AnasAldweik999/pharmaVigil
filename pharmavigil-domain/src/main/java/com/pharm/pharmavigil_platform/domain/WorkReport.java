package com.pharm.pharmavigil_platform.domain;

import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkReport {

    private UUID id;
    private User staffUser;
    private UUID shiftId;
    private String shiftName;
    private LocalDate reportDate;
    @Builder.Default
    private List<WorkReportMachine> machines = new ArrayList<>();
    private Instant createdAt;
    private Instant updatedAt;
}
