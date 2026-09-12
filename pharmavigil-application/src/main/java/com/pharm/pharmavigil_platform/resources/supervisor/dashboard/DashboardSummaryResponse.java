package com.pharm.pharmavigil_platform.resources.supervisor.dashboard;

public record DashboardSummaryResponse(
        SummaryCardsResponse summaryCards,
        String groupedDataLink
) {}
