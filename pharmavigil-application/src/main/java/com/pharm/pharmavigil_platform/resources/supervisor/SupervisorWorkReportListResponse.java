package com.pharm.pharmavigil_platform.resources.supervisor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record SupervisorWorkReportListResponse(
        UUID id,
        String staffName,
        String staffUsername,
        String staffEmail,
        LocalDate reportDate,
        Instant createdAt,
        String shiftName
) {}
