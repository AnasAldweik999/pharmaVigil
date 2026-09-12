package com.pharm.pharmavigil_platform.service.dashboard;

import com.pharm.pharmavigil_platform.domain.SummaryGroupBy;
import com.pharm.pharmavigil_platform.resources.supervisor.dashboard.DashboardSummaryResponse;
import com.pharm.pharmavigil_platform.resources.supervisor.dashboard.SummaryCardsResponse;
import com.pharm.pharmavigil_platform.util.DurationFormatter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardSummaryQuery {

    private final EntityManager em;
    private final DashboardQueryLoader queryLoader;
    private final DashboardFilterBuilder filterBuilder;
    private final DashboardLinksBuilder linksBuilder;
    private final DurationFormatter durationFormatter;

    private SummaryCardsResponse getSummaryCards(LocalDate startDate, LocalDate endDate,
                                                 List<UUID> shiftIds, List<UUID> staffIds,
                                                 List<UUID> machineIds) {
        String filter = filterBuilder.rangeFilter(shiftIds, staffIds, machineIds);

        Query pq = em.createQuery(queryLoader.get("summary-products").replace("{filter}", filter));
        filterBuilder.bindRange(pq, startDate, endDate, shiftIds, staffIds, machineIds);
        Object[] pr = (Object[]) pq.getSingleResult();

        Query sq = em.createQuery(queryLoader.get("summary-stops").replace("{filter}", filter));
        filterBuilder.bindRange(sq, startDate, endDate, shiftIds, staffIds, machineIds);
        Object[] sr = (Object[]) sq.getSingleResult();

        return new SummaryCardsResponse(
                DashboardFilterBuilder.toLong(pr[0]),
                DashboardFilterBuilder.toLong(sr[0]),
                durationFormatter.toHHMM(DashboardFilterBuilder.toLong(sr[1])),
                DashboardFilterBuilder.toLong(pr[1]),
                DashboardFilterBuilder.toLong(pr[2]),
                0
        );
    }

    private SummaryCardsResponse getStopSummaryCards(LocalDate startDate, LocalDate endDate,
                                                      List<UUID> shiftIds, List<UUID> staffIds,
                                                      List<UUID> machineIds) {
        String filter = filterBuilder.rangeFilter(shiftIds, staffIds, machineIds);

        Query q1 = em.createQuery(queryLoader.get("stop-summary-aggregate").replace("{filter}", filter));
        filterBuilder.bindRange(q1, startDate, endDate, shiftIds, staffIds, machineIds);
        Object[] r = (Object[]) q1.getSingleResult();

        Query q2 = em.createQuery(queryLoader.get("stop-summary-machines").replace("{filter}", filter));
        filterBuilder.bindRange(q2, startDate, endDate, shiftIds, staffIds, machineIds);

        return new SummaryCardsResponse(
                DashboardFilterBuilder.toLong(r[1]),
                DashboardFilterBuilder.toLong(r[0]),
                durationFormatter.toHHMM(DashboardFilterBuilder.toLong(r[2])),
                0,
                0,
                DashboardFilterBuilder.toLong(q2.getSingleResult())
        );
    }

    public DashboardSummaryResponse getSummary(SummaryGroupBy groupBy, LocalDate startDate, LocalDate endDate,
                                                List<UUID> shiftIds, List<UUID> staffIds,
                                                List<UUID> machineIds) {
        SummaryCardsResponse cards = (groupBy == SummaryGroupBy.STOP)
                ? getStopSummaryCards(startDate, endDate, shiftIds, staffIds, machineIds)
                : getSummaryCards(startDate, endDate, shiftIds, staffIds, machineIds);
        String link = linksBuilder.buildGroupedLink(groupBy, startDate, endDate, shiftIds, staffIds, machineIds);
        return new DashboardSummaryResponse(cards, link);
    }

}
