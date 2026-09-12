package com.pharm.pharmavigil_platform.resources.supervisor.dashboard;

import java.util.Map;
import java.util.UUID;

public record StopGroupRow(
        UUID stopTypeId,
        String stopName,
        long totalMachines,
        long totalProducts,
        String totalDowntimeMinutes,
        Map<String, String> _links
) {}
