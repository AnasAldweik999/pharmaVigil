package com.pharm.pharmavigil_platform.resources.supervisor.dashboard;

import java.time.LocalDate;
import java.util.Map;

public record DateGroupRow(
        LocalDate date,
        long productCount,
        long stopCount,
        String downtimeMinutes,
        long holdCount,
        long deviationCount,
        Map<String, String> _links
) {}
