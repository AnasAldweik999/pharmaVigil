package com.pharm.pharmavigil_platform.resources.supervisor.dashboard;

import java.util.Map;
import java.util.UUID;

public record StaffGroupRow(
        UUID staffId,
        String staffName,
        String staffUsername,
        String staffEmail,
        long productCount,
        long stopCount,
        String downtimeMinutes,
        long holdCount,
        long deviationCount,
        Map<String, String> _links
) {}
