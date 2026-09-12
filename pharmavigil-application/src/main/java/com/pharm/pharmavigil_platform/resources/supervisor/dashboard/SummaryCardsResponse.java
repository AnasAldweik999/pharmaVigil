package com.pharm.pharmavigil_platform.resources.supervisor.dashboard;

public record SummaryCardsResponse(
        long totalProducts,
        long totalStops,
        String totalDowntimeMinutes,
        long totalHolds,
        long totalDeviations,
        long totalMachines
) {}
