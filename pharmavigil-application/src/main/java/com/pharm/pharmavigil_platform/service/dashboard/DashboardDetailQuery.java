package com.pharm.pharmavigil_platform.service.dashboard;

import com.pharm.pharmavigil_platform.resources.supervisor.dashboard.*;
import com.pharm.pharmavigil_platform.util.DurationFormatter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardDetailQuery {

    private final EntityManager em;
    private final DashboardQueryLoader queryLoader;
    private final DashboardFilterBuilder filterBuilder;
    private final DashboardLinksBuilder linksBuilder;
    private final DurationFormatter durationFormatter;

    // -------------------------------------------------------------------------
    // Products by dimension
    // -------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public PageResponse<MachineProductRow> getMachineProducts(UUID machineId, LocalDate startDate, LocalDate endDate,
                                                               List<UUID> shiftIds, List<UUID> staffIds,
                                                               int page, int size) {
        String filter = machineFilter(shiftIds, staffIds);

        Query q = em.createQuery(queryLoader.get("detail-machine-products").replace("{filter}", filter));
        bindMachine(q, machineId, startDate, endDate, shiftIds, staffIds);
        q.setFirstResult(page * size);
        q.setMaxResults(size);
        List<Object[]> rows = q.getResultList();

        Query cq = em.createQuery(queryLoader.get("detail-machine-products-count").replace("{filter}", filter));
        bindMachine(cq, machineId, startDate, endDate, shiftIds, staffIds);
        long total = DashboardFilterBuilder.toLong(cq.getSingleResult());

        List<UUID> productIds = rows.stream().map(r -> (UUID) r[0]).toList();
        Map<UUID, long[]> stopData = fetchStopData(productIds);

        int totalPages = (int) Math.ceil((double) total / size);
        List<MachineProductRow> content = rows.stream().map(r -> new MachineProductRow(
                (UUID) r[1], (String) r[2], (String) r[4], (String) r[3],
                r[5] == null ? null : r[5].toString(), (String) r[6],
                (LocalDate) r[7], (UUID) r[8], (String) r[9],
                (UUID) r[10], (String) r[11], (String) r[12], DashboardFilterBuilder.toLong(r[13]), (String) r[14],
                stopCount(stopData, (UUID) r[0]),
                stopsLink((UUID) r[0]),
                durationFormatter.toHHMM(stopDuration(stopData, (UUID) r[0])),
                deviation((Boolean) r[15], (String) r[16]),
                hold((Boolean) r[17], (String) r[18]),
                (String) r[19],
                completedStages(r[20])
        )).toList();

        return new PageResponse<>(content, page, size, total, totalPages);
    }

    @SuppressWarnings("unchecked")
    public PageResponse<StaffProductRow> getStaffProducts(UUID staffId, LocalDate startDate, LocalDate endDate,
                                                           List<UUID> shiftIds, List<UUID> machineIds,
                                                           int page, int size) {
        String filter = staffFilter(shiftIds, machineIds);

        Query q = em.createQuery(queryLoader.get("detail-staff-products").replace("{filter}", filter));
        bindStaff(q, staffId, startDate, endDate, shiftIds, machineIds);
        q.setFirstResult(page * size);
        q.setMaxResults(size);
        List<Object[]> rows = q.getResultList();

        Query cq = em.createQuery(queryLoader.get("detail-staff-products-count").replace("{filter}", filter));
        bindStaff(cq, staffId, startDate, endDate, shiftIds, machineIds);
        long total = DashboardFilterBuilder.toLong(cq.getSingleResult());

        List<UUID> productIds = rows.stream().map(r -> (UUID) r[0]).toList();
        Map<UUID, long[]> stopData = fetchStopData(productIds);

        int totalPages = (int) Math.ceil((double) total / size);
        List<StaffProductRow> content = rows.stream().map(r -> new StaffProductRow(
                (UUID) r[1], (String) r[2], r[3] == null ? null : r[3].toString(), (String) r[4],
                (LocalDate) r[5], (UUID) r[6], (String) r[7],
                (UUID) r[8], (String) r[9], (String) r[10], DashboardFilterBuilder.toLong(r[11]), (String) r[12],
                stopCount(stopData, (UUID) r[0]),
                stopsLink((UUID) r[0]),
                durationFormatter.toHHMM(stopDuration(stopData, (UUID) r[0])),
                deviation((Boolean) r[13], (String) r[14]),
                hold((Boolean) r[15], (String) r[16]),
                (String) r[17],
                completedStages(r[18])
        )).toList();

        return new PageResponse<>(content, page, size, total, totalPages);
    }

    @SuppressWarnings("unchecked")
    public PageResponse<ShiftProductRow> getShiftProducts(UUID shiftId, LocalDate startDate, LocalDate endDate,
                                                           List<UUID> staffIds, List<UUID> machineIds,
                                                           int page, int size) {
        String filter = shiftFilter(staffIds, machineIds);

        Query q = em.createQuery(queryLoader.get("detail-shift-products").replace("{filter}", filter));
        bindShift(q, shiftId, startDate, endDate, staffIds, machineIds);
        q.setFirstResult(page * size);
        q.setMaxResults(size);
        List<Object[]> rows = q.getResultList();

        Query cq = em.createQuery(queryLoader.get("detail-shift-products-count").replace("{filter}", filter));
        bindShift(cq, shiftId, startDate, endDate, staffIds, machineIds);
        long total = DashboardFilterBuilder.toLong(cq.getSingleResult());

        List<UUID> productIds = rows.stream().map(r -> (UUID) r[0]).toList();
        Map<UUID, long[]> stopData = fetchStopData(productIds);

        int totalPages = (int) Math.ceil((double) total / size);
        List<ShiftProductRow> content = rows.stream().map(r -> new ShiftProductRow(
                (UUID) r[1], (String) r[2], r[3] == null ? null : r[3].toString(), (String) r[4],
                (UUID) r[5], (String) r[6], (String) r[7], (String) r[8], (LocalDate) r[9],
                (UUID) r[10], (String) r[11], (String) r[12], DashboardFilterBuilder.toLong(r[13]), (String) r[14],
                stopCount(stopData, (UUID) r[0]),
                stopsLink((UUID) r[0]),
                durationFormatter.toHHMM(stopDuration(stopData, (UUID) r[0])),
                deviation((Boolean) r[15], (String) r[16]),
                hold((Boolean) r[17], (String) r[18]),
                (String) r[19],
                completedStages(r[20])
        )).toList();

        return new PageResponse<>(content, page, size, total, totalPages);
    }

    @SuppressWarnings("unchecked")
    public PageResponse<DateProductRow> getDateProducts(LocalDate date,
                                                         List<UUID> shiftIds, List<UUID> staffIds,
                                                         List<UUID> machineIds, int page, int size) {
        String filter = filterBuilder.dateFilter(shiftIds, staffIds, machineIds);

        Query q = em.createQuery(queryLoader.get("detail-date-products").replace("{filter}", filter));
        filterBuilder.bindDate(q, date, shiftIds, staffIds, machineIds);
        q.setFirstResult(page * size);
        q.setMaxResults(size);
        List<Object[]> rows = q.getResultList();

        Query cq = em.createQuery(queryLoader.get("detail-date-products-count").replace("{filter}", filter));
        filterBuilder.bindDate(cq, date, shiftIds, staffIds, machineIds);
        long total = DashboardFilterBuilder.toLong(cq.getSingleResult());

        List<UUID> productIds = rows.stream().map(r -> (UUID) r[0]).toList();
        Map<UUID, long[]> stopData = fetchStopData(productIds);

        int totalPages = (int) Math.ceil((double) total / size);
        List<DateProductRow> content = rows.stream().map(r -> new DateProductRow(
                (UUID) r[1], (String) r[2], r[3] == null ? null : r[3].toString(), (String) r[4],
                (UUID) r[5], (String) r[6], (String) r[7], (String) r[8],
                (UUID) r[9], (String) r[10],
                (UUID) r[11], (String) r[12], (String) r[13], DashboardFilterBuilder.toLong(r[14]), (String) r[15],
                stopCount(stopData, (UUID) r[0]),
                stopsLink((UUID) r[0]),
                durationFormatter.toHHMM(stopDuration(stopData, (UUID) r[0])),
                deviation((Boolean) r[16], (String) r[17]),
                hold((Boolean) r[18], (String) r[19]),
                (String) r[20],
                completedStages(r[21])
        )).toList();

        return new PageResponse<>(content, page, size, total, totalPages);
    }

    // -------------------------------------------------------------------------
    // Stop dimension
    // -------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public PageResponse<StopMachineRow> getStopMachines(UUID stopTypeId, String stopName, LocalDate startDate, LocalDate endDate,
                                                         List<UUID> shiftIds, List<UUID> staffIds,
                                                         List<UUID> machineIds, int page, int size) {
        String filter = stopMachineFilter(stopTypeId, stopName, shiftIds, staffIds, machineIds);

        Query dq = em.createQuery(queryLoader.get("detail-stop-machines").replace("{filter}", filter));
        bindStopMachine(dq, stopTypeId, stopName, startDate, endDate, shiftIds, staffIds, machineIds);
        dq.setFirstResult(page * size);
        dq.setMaxResults(size);
        List<Object[]> rows = dq.getResultList();

        Query cq = em.createQuery(queryLoader.get("detail-stop-machines-count").replace("{filter}", filter));
        bindStopMachine(cq, stopTypeId, stopName, startDate, endDate, shiftIds, staffIds, machineIds);
        long total = DashboardFilterBuilder.toLong(cq.getSingleResult());

        int totalPages = (int) Math.ceil((double) total / size);
        List<StopMachineRow> content = rows.stream().map(r -> new StopMachineRow(
                (UUID) r[1], (String) r[2], r[3] == null ? null : r[3].toString(), (String) r[4],
                (UUID) r[5], (String) r[6], (String) r[7], (String) r[8],
                (UUID) r[9], (String) r[10], (LocalDate) r[11],
                DashboardFilterBuilder.toLong(r[12]), durationFormatter.toHHMM(DashboardFilterBuilder.toLong(r[13])),
                linksBuilder.stopMachineLinks((UUID) r[0], stopTypeId, stopName)
        )).toList();

        return new PageResponse<>(content, page, size, total, totalPages);
    }

    @SuppressWarnings("unchecked")
    public PageResponse<StopProductRow> getStopProducts(UUID workReportMachineId, UUID stopTypeId, String stopName,
                                                         int page, int size) {
        String filter = stopProductsFilter(stopTypeId, stopName);

        Query dq = em.createQuery(queryLoader.get("detail-stop-products").replace("{filter}", filter));
        bindStopProducts(dq, workReportMachineId, stopTypeId, stopName);
        dq.setFirstResult(page * size);
        dq.setMaxResults(size);
        List<Object[]> rows = dq.getResultList();

        Query cq = em.createQuery(queryLoader.get("detail-stop-products-count").replace("{filter}", filter));
        bindStopProducts(cq, workReportMachineId, stopTypeId, stopName);
        long total = DashboardFilterBuilder.toLong(cq.getSingleResult());

        int totalPages = (int) Math.ceil((double) total / size);
        List<StopProductRow> content = rows.stream()
                .map(r -> new StopProductRow((UUID) r[0], (String) r[1], (String) r[2], (String) r[3],
                        completedStages(r[4])))
                .toList();

        return new PageResponse<>(content, page, size, total, totalPages);
    }

    @SuppressWarnings("unchecked")
    public PageResponse<ProductStopRow> getProductStops(UUID productId, int page, int size) {
        Query q = em.createQuery(queryLoader.get("detail-product-stops"));
        q.setParameter("productId", productId);
        q.setFirstResult(page * size);
        q.setMaxResults(size);
        List<Object[]> rows = q.getResultList();

        Query cq = em.createQuery(queryLoader.get("detail-product-stops-count"));
        cq.setParameter("productId", productId);
        long total = DashboardFilterBuilder.toLong(cq.getSingleResult());

        int totalPages = (int) Math.ceil((double) total / size);
        List<ProductStopRow> content = rows.stream()
                .map(r -> new ProductStopRow((UUID) r[0], (String) r[1], durationFormatter.toHHMM(DashboardFilterBuilder.toLong(r[2])), (String) r[3]))
                .toList();

        return new PageResponse<>(content, page, size, total, totalPages);
    }

    // -------------------------------------------------------------------------
    // Filter builders
    // -------------------------------------------------------------------------

    private String machineFilter(List<UUID> shiftIds, List<UUID> staffIds) {
        StringBuilder w = new StringBuilder("WHERE mach.id = :machineId AND wr.reportDate BETWEEN :startDate AND :endDate");
        if (filterBuilder.notEmpty(shiftIds)) w.append(" AND sh.id IN :shiftIds");
        if (filterBuilder.notEmpty(staffIds)) w.append(" AND u.id IN :staffIds");
        return w.toString();
    }

    private String staffFilter(List<UUID> shiftIds, List<UUID> machineIds) {
        StringBuilder w = new StringBuilder("WHERE u.id = :staffId AND wr.reportDate BETWEEN :startDate AND :endDate");
        if (filterBuilder.notEmpty(shiftIds))   w.append(" AND sh.id IN :shiftIds");
        if (filterBuilder.notEmpty(machineIds)) w.append(" AND mach.id IN :machineIds");
        return w.toString();
    }

    private String shiftFilter(List<UUID> staffIds, List<UUID> machineIds) {
        StringBuilder w = new StringBuilder("WHERE sh.id = :shiftId AND wr.reportDate BETWEEN :startDate AND :endDate");
        if (filterBuilder.notEmpty(staffIds))   w.append(" AND u.id IN :staffIds");
        if (filterBuilder.notEmpty(machineIds)) w.append(" AND mach.id IN :machineIds");
        return w.toString();
    }

    private String stopMachineFilter(UUID stopTypeId, String stopName, List<UUID> shiftIds, List<UUID> staffIds, List<UUID> machineIds) {
        StringBuilder w = new StringBuilder("WHERE ");
        w.append(stopTypeId != null ? "st.id = :stopTypeId" : "s.stopTypeName = :stopName");
        w.append(" AND wr.reportDate BETWEEN :startDate AND :endDate");
        if (filterBuilder.notEmpty(shiftIds))   w.append(" AND sh.id IN :shiftIds");
        if (filterBuilder.notEmpty(staffIds))   w.append(" AND u.id IN :staffIds");
        if (filterBuilder.notEmpty(machineIds)) w.append(" AND mach.id IN :machineIds");
        return w.toString();
    }

    private String stopProductsFilter(UUID stopTypeId, String stopName) {
        return stopTypeId != null ? "st.id = :stopTypeId" : "s.stopTypeName = :stopName";
    }

    // -------------------------------------------------------------------------
    // Parameter binders
    // -------------------------------------------------------------------------

    private void bindMachine(Query q, UUID machineId, LocalDate startDate, LocalDate endDate,
                              List<UUID> shiftIds, List<UUID> staffIds) {
        q.setParameter("machineId", machineId);
        q.setParameter("startDate", startDate);
        q.setParameter("endDate", endDate);
        if (filterBuilder.notEmpty(shiftIds)) q.setParameter("shiftIds", shiftIds);
        if (filterBuilder.notEmpty(staffIds)) q.setParameter("staffIds", staffIds);
    }

    private void bindStaff(Query q, UUID staffId, LocalDate startDate, LocalDate endDate,
                            List<UUID> shiftIds, List<UUID> machineIds) {
        q.setParameter("staffId", staffId);
        q.setParameter("startDate", startDate);
        q.setParameter("endDate", endDate);
        if (filterBuilder.notEmpty(shiftIds))   q.setParameter("shiftIds", shiftIds);
        if (filterBuilder.notEmpty(machineIds)) q.setParameter("machineIds", machineIds);
    }

    private void bindShift(Query q, UUID shiftId, LocalDate startDate, LocalDate endDate,
                            List<UUID> staffIds, List<UUID> machineIds) {
        q.setParameter("shiftId", shiftId);
        q.setParameter("startDate", startDate);
        q.setParameter("endDate", endDate);
        if (filterBuilder.notEmpty(staffIds))   q.setParameter("staffIds", staffIds);
        if (filterBuilder.notEmpty(machineIds)) q.setParameter("machineIds", machineIds);
    }

    private void bindStopMachine(Query q, UUID stopTypeId, String stopName, LocalDate startDate, LocalDate endDate,
                                  List<UUID> shiftIds, List<UUID> staffIds, List<UUID> machineIds) {
        if (stopTypeId != null) q.setParameter("stopTypeId", stopTypeId);
        else q.setParameter("stopName", stopName.trim());
        q.setParameter("startDate", startDate);
        q.setParameter("endDate", endDate);
        if (filterBuilder.notEmpty(shiftIds))   q.setParameter("shiftIds", shiftIds);
        if (filterBuilder.notEmpty(staffIds))   q.setParameter("staffIds", staffIds);
        if (filterBuilder.notEmpty(machineIds)) q.setParameter("machineIds", machineIds);
    }

    private void bindStopProducts(Query q, UUID workReportMachineId, UUID stopTypeId, String stopName) {
        q.setParameter("wmId", workReportMachineId);
        if (stopTypeId != null) q.setParameter("stopTypeId", stopTypeId);
        else q.setParameter("stopName", stopName.trim());
    }

    // -------------------------------------------------------------------------
    // Utility helpers
    // -------------------------------------------------------------------------

    private Map<UUID, long[]> fetchStopData(List<UUID> productIds) {
        if (productIds.isEmpty()) return Map.of();
        List<Object[]> rows = em.createQuery(queryLoader.get("fetch-stop-data"), Object[].class)
                .setParameter("ids", productIds)
                .getResultList();

        Map<UUID, long[]> result = new LinkedHashMap<>();
        for (Object[] r : rows) {
            UUID productId = (UUID) r[0];
            long[] acc = result.computeIfAbsent(productId, k -> new long[]{0, 0});
            acc[0]++;
            acc[1] += DashboardFilterBuilder.toLong(r[1]);
        }
        return result;
    }

    private int stopCount(Map<UUID, long[]> stopData, UUID productId) {
        long[] d = stopData.get(productId);
        return d == null ? 0 : (int) d[0];
    }

    private long stopDuration(Map<UUID, long[]> stopData, UUID productId) {
        long[] d = stopData.get(productId);
        return d == null ? 0L : d[1];
    }

    private String stopsLink(UUID productId) {
        return "/api/supervisor/products/stops?productId=" + productId;
    }

    private String deviation(boolean flag, String details) { return flag ? details : null; }
    private String hold(boolean flag, String details)      { return flag ? details : null; }

    @SuppressWarnings("unchecked")
    private String completedStages(Object stages) {
        if (!(stages instanceof Map<?, ?> map)) return null;
        return ((Map<String, Boolean>) map).entrySet().stream()
                .filter(e -> Boolean.TRUE.equals(e.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.joining(", "));
    }

}
