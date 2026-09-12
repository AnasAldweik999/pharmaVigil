package com.pharm.pharmavigil_platform.service.dashboard;

import com.pharm.pharmavigil_platform.domain.SummaryGroupBy;
import com.pharm.pharmavigil_platform.resources.supervisor.dashboard.*;
import com.pharm.pharmavigil_platform.util.DurationFormatter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardGroupQuery {

    private final EntityManager em;
    private final DashboardQueryLoader queryLoader;
    private final DashboardFilterBuilder filterBuilder;
    private final DashboardLinksBuilder linksBuilder;
    private final DurationFormatter durationFormatter;

    public PageResponse<?> getGroupedData(SummaryGroupBy groupBy, LocalDate startDate, LocalDate endDate,
                                           List<UUID> shiftIds, List<UUID> staffIds, List<UUID> machineIds,
                                           int page, int size) {
        return switch (groupBy) {
            case MACHINE -> getGroupedByMachine(startDate, endDate, shiftIds, staffIds, machineIds, page, size);
            case STAFF   -> getGroupedByStaff(startDate, endDate, shiftIds, staffIds, machineIds, page, size);
            case SHIFT   -> getGroupedByShift(startDate, endDate, shiftIds, staffIds, machineIds, page, size);
            case DATE    -> getGroupedByDate(startDate, endDate, shiftIds, staffIds, machineIds, page, size);
            case STOP    -> getGroupedByStop(startDate, endDate, shiftIds, staffIds, machineIds, page, size);
        };
    }

    @SuppressWarnings("unchecked")
    private PageResponse<MachineGroupRow> getGroupedByMachine(LocalDate startDate, LocalDate endDate,
                                                               List<UUID> shiftIds, List<UUID> staffIds,
                                                               List<UUID> machineIds, int page, int size) {
        String filter = filterBuilder.rangeFilter(shiftIds, staffIds, machineIds);

        Query pq = em.createQuery(queryLoader.get("grouped-machine-products").replace("{filter}", filter));
        filterBuilder.bindRange(pq, startDate, endDate, shiftIds, staffIds, machineIds);
        pq.setFirstResult(page * size);
        pq.setMaxResults(size);
        List<Object[]> prodRows = pq.getResultList();

        Query cq = em.createQuery(queryLoader.get("grouped-machine-count").replace("{filter}", filter));
        filterBuilder.bindRange(cq, startDate, endDate, shiftIds, staffIds, machineIds);
        long total = DashboardFilterBuilder.toLong(cq.getSingleResult());

        if (prodRows.isEmpty()) {
            return new PageResponse<>(List.of(), page, size, total, 0);
        }

        List<UUID> pageKeys = prodRows.stream().map(r -> (UUID) r[0]).toList();

        Query sq = em.createQuery(queryLoader.get("grouped-machine-stops-page").replace("{filter}", filter));
        filterBuilder.bindRange(sq, startDate, endDate, shiftIds, staffIds, machineIds);
        sq.setParameter("pageKeys", pageKeys);
        Map<UUID, Object[]> stopMap = ((List<Object[]>) sq.getResultList()).stream()
                .collect(Collectors.toMap(r -> (UUID) r[0], r -> r));

        int totalPages = (int) Math.ceil((double) total / size);
        List<MachineGroupRow> content = prodRows.stream().map(r -> {
            UUID machineId = (UUID) r[0];
            Object[] sd = stopMap.getOrDefault(machineId, new Object[]{machineId, 0L, 0L});
            return new MachineGroupRow(
                    machineId, (String) r[1], (String) r[2],
                    DashboardFilterBuilder.toLong(r[3]),
                    DashboardFilterBuilder.toLong(sd[1]), durationFormatter.toHHMM(DashboardFilterBuilder.toLong(sd[2])),
                    DashboardFilterBuilder.toLong(r[4]), DashboardFilterBuilder.toLong(r[5]),
                    linksBuilder.machineLinks(machineId, startDate, endDate, shiftIds, staffIds)
            );
        }).toList();

        return new PageResponse<>(content, page, size, total, totalPages);
    }

    @SuppressWarnings("unchecked")
    private PageResponse<StaffGroupRow> getGroupedByStaff(LocalDate startDate, LocalDate endDate,
                                                           List<UUID> shiftIds, List<UUID> staffIds,
                                                           List<UUID> machineIds, int page, int size) {
        String filter = filterBuilder.rangeFilter(shiftIds, staffIds, machineIds);

        Query pq = em.createQuery(queryLoader.get("grouped-staff-products").replace("{filter}", filter));
        filterBuilder.bindRange(pq, startDate, endDate, shiftIds, staffIds, machineIds);
        pq.setFirstResult(page * size);
        pq.setMaxResults(size);
        List<Object[]> prodRows = pq.getResultList();

        Query cq = em.createQuery(queryLoader.get("grouped-staff-count").replace("{filter}", filter));
        filterBuilder.bindRange(cq, startDate, endDate, shiftIds, staffIds, machineIds);
        long total = DashboardFilterBuilder.toLong(cq.getSingleResult());

        if (prodRows.isEmpty()) return new PageResponse<>(List.of(), page, size, total, 0);

        List<UUID> pageKeys = prodRows.stream().map(r -> (UUID) r[0]).toList();

        Query sq = em.createQuery(queryLoader.get("grouped-staff-stops-page").replace("{filter}", filter));
        filterBuilder.bindRange(sq, startDate, endDate, shiftIds, staffIds, machineIds);
        sq.setParameter("pageKeys", pageKeys);
        Map<UUID, Object[]> stopMap = ((List<Object[]>) sq.getResultList()).stream()
                .collect(Collectors.toMap(r -> (UUID) r[0], r -> r));

        int totalPages = (int) Math.ceil((double) total / size);
        List<StaffGroupRow> content = prodRows.stream().map(r -> {
            UUID staffId = (UUID) r[0];
            Object[] sd = stopMap.getOrDefault(staffId, new Object[]{staffId, 0L, 0L});
            return new StaffGroupRow(
                    staffId, (String) r[1], (String) r[2], (String) r[3],
                    DashboardFilterBuilder.toLong(r[4]),
                    DashboardFilterBuilder.toLong(sd[1]), durationFormatter.toHHMM(DashboardFilterBuilder.toLong(sd[2])),
                    DashboardFilterBuilder.toLong(r[5]), DashboardFilterBuilder.toLong(r[6]),
                    linksBuilder.staffLinks(staffId, startDate, endDate, shiftIds, machineIds)
            );
        }).toList();

        return new PageResponse<>(content, page, size, total, totalPages);
    }

    @SuppressWarnings("unchecked")
    private PageResponse<ShiftGroupRow> getGroupedByShift(LocalDate startDate, LocalDate endDate,
                                                           List<UUID> shiftIds, List<UUID> staffIds,
                                                           List<UUID> machineIds, int page, int size) {
        String filter = filterBuilder.rangeFilter(shiftIds, staffIds, machineIds);

        Query pq = em.createQuery(queryLoader.get("grouped-shift-products").replace("{filter}", filter));
        filterBuilder.bindRange(pq, startDate, endDate, shiftIds, staffIds, machineIds);
        pq.setFirstResult(page * size);
        pq.setMaxResults(size);
        List<Object[]> prodRows = pq.getResultList();

        Query cq = em.createQuery(queryLoader.get("grouped-shift-count").replace("{filter}", filter));
        filterBuilder.bindRange(cq, startDate, endDate, shiftIds, staffIds, machineIds);
        long total = DashboardFilterBuilder.toLong(cq.getSingleResult());

        if (prodRows.isEmpty()) return new PageResponse<>(List.of(), page, size, total, 0);

        List<UUID> pageKeys = prodRows.stream().map(r -> (UUID) r[0]).toList();

        Query sq = em.createQuery(queryLoader.get("grouped-shift-stops-page").replace("{filter}", filter));
        filterBuilder.bindRange(sq, startDate, endDate, shiftIds, staffIds, machineIds);
        sq.setParameter("pageKeys", pageKeys);
        Map<UUID, Object[]> stopMap = ((List<Object[]>) sq.getResultList()).stream()
                .collect(Collectors.toMap(r -> (UUID) r[0], r -> r));

        int totalPages = (int) Math.ceil((double) total / size);
        List<ShiftGroupRow> content = prodRows.stream().map(r -> {
            UUID shiftId = (UUID) r[0];
            Object[] sd = stopMap.getOrDefault(shiftId, new Object[]{shiftId, 0L, 0L});
            return new ShiftGroupRow(
                    shiftId, (String) r[1],
                    DashboardFilterBuilder.toLong(r[2]),
                    DashboardFilterBuilder.toLong(sd[1]), durationFormatter.toHHMM(DashboardFilterBuilder.toLong(sd[2])),
                    DashboardFilterBuilder.toLong(r[3]), DashboardFilterBuilder.toLong(r[4]),
                    linksBuilder.shiftLinks(shiftId, startDate, endDate, staffIds, machineIds)
            );
        }).toList();

        return new PageResponse<>(content, page, size, total, totalPages);
    }

    @SuppressWarnings("unchecked")
    private PageResponse<DateGroupRow> getGroupedByDate(LocalDate startDate, LocalDate endDate,
                                                         List<UUID> shiftIds, List<UUID> staffIds,
                                                         List<UUID> machineIds, int page, int size) {
        String filter = filterBuilder.rangeFilter(shiftIds, staffIds, machineIds);

        Query pq = em.createQuery(queryLoader.get("grouped-date-products").replace("{filter}", filter));
        filterBuilder.bindRange(pq, startDate, endDate, shiftIds, staffIds, machineIds);
        pq.setFirstResult(page * size);
        pq.setMaxResults(size);
        List<Object[]> prodRows = pq.getResultList();

        Query cq = em.createQuery(queryLoader.get("grouped-date-count").replace("{filter}", filter));
        filterBuilder.bindRange(cq, startDate, endDate, shiftIds, staffIds, machineIds);
        long total = DashboardFilterBuilder.toLong(cq.getSingleResult());

        if (prodRows.isEmpty()) return new PageResponse<>(List.of(), page, size, total, 0);

        List<LocalDate> pageKeys = prodRows.stream().map(r -> (LocalDate) r[0]).toList();

        Query sq = em.createQuery(queryLoader.get("grouped-date-stops-page").replace("{filter}", filter));
        filterBuilder.bindRange(sq, startDate, endDate, shiftIds, staffIds, machineIds);
        sq.setParameter("pageKeys", pageKeys);
        Map<LocalDate, Object[]> stopMap = ((List<Object[]>) sq.getResultList()).stream()
                .collect(Collectors.toMap(r -> (LocalDate) r[0], r -> r));

        int totalPages = (int) Math.ceil((double) total / size);
        List<DateGroupRow> content = prodRows.stream().map(r -> {
            LocalDate date = (LocalDate) r[0];
            Object[] sd = stopMap.getOrDefault(date, new Object[]{date, 0L, 0L});
            return new DateGroupRow(
                    date,
                    DashboardFilterBuilder.toLong(r[1]),
                    DashboardFilterBuilder.toLong(sd[1]), durationFormatter.toHHMM(DashboardFilterBuilder.toLong(sd[2])),
                    DashboardFilterBuilder.toLong(r[2]), DashboardFilterBuilder.toLong(r[3]),
                    linksBuilder.dateLinks(date, shiftIds, staffIds, machineIds)
            );
        }).toList();

        return new PageResponse<>(content, page, size, total, totalPages);
    }

    @SuppressWarnings("unchecked")
    private PageResponse<StopGroupRow> getGroupedByStop(LocalDate startDate, LocalDate endDate,
                                                         List<UUID> shiftIds, List<UUID> staffIds,
                                                         List<UUID> machineIds, int page, int size) {
        String filter = filterBuilder.rangeFilter(shiftIds, staffIds, machineIds);

        Query dq = em.createQuery(queryLoader.get("grouped-stop-data").replace("{filter}", filter));
        filterBuilder.bindRange(dq, startDate, endDate, shiftIds, staffIds, machineIds);
        dq.setFirstResult(page * size);
        dq.setMaxResults(size);
        List<Object[]> rows = dq.getResultList();

        Query cq = em.createQuery(queryLoader.get("grouped-stop-count").replace("{filter}", filter));
        filterBuilder.bindRange(cq, startDate, endDate, shiftIds, staffIds, machineIds);
        long total = DashboardFilterBuilder.toLong(cq.getSingleResult());

        int totalPages = (int) Math.ceil((double) total / size);
        List<StopGroupRow> content = rows.stream().map(r -> {
            UUID stopTypeId = (UUID) r[0];
            String stopTypeName = (String) r[1];
            return new StopGroupRow(
                    stopTypeId, stopTypeName,
                    DashboardFilterBuilder.toLong(r[2]), DashboardFilterBuilder.toLong(r[3]),
                    durationFormatter.toHHMM(DashboardFilterBuilder.toLong(r[4])),
                    linksBuilder.stopGroupLinks(stopTypeId, stopTypeName, startDate, endDate, shiftIds, staffIds, machineIds)
            );
        }).toList();

        return new PageResponse<>(content, page, size, total, totalPages);
    }
}
