package com.pharm.pharmavigil_platform.resources.supervisor.dashboard;

import java.util.Map;
import java.util.UUID;

public record ShiftGroupRow(
        UUID shiftId,
        String shiftName,
        long productCount,
        long stopCount,
        String downtimeMinutes,
        long holdCount,
        long deviationCount,
        Map<String, String> _links
) {}
