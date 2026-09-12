package com.pharm.pharmavigil_platform.service;

import com.pharm.pharmavigil_platform.domain.SummaryGroupBy;
import com.pharm.pharmavigil_platform.resources.supervisor.dashboard.*;
import com.pharm.pharmavigil_platform.service.dashboard.DashboardDetailQuery;
import com.pharm.pharmavigil_platform.service.dashboard.DashboardGroupQuery;
import com.pharm.pharmavigil_platform.service.dashboard.DashboardSummaryQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SupervisorDashboardService {

    private final DashboardSummaryQuery summaryQuery;
    private final DashboardGroupQuery groupQuery;
    private final DashboardDetailQuery detailQuery;

    public DashboardSummaryResponse getSummary(SummaryGroupBy groupBy, LocalDate startDate, LocalDate endDate,
                                                List<UUID> shiftIds, List<UUID> staffIds, List<UUID> machineIds) {
        return summaryQuery.getSummary(groupBy, startDate, endDate, shiftIds, staffIds, machineIds);
    }

    public PageResponse<?> getGroupedData(SummaryGroupBy groupBy, LocalDate startDate, LocalDate endDate,
                                           List<UUID> shiftIds, List<UUID> staffIds, List<UUID> machineIds,
                                           int page, int size) {
        return groupQuery.getGroupedData(groupBy, startDate, endDate, shiftIds, staffIds, machineIds, page, size);
    }

    public PageResponse<MachineProductRow> getMachineProducts(UUID machineId, LocalDate startDate, LocalDate endDate,
                                                               List<UUID> shiftIds, List<UUID> staffIds,
                                                               int page, int size) {
        return detailQuery.getMachineProducts(machineId, startDate, endDate, shiftIds, staffIds, page, size);
    }

    public PageResponse<StaffProductRow> getStaffProducts(UUID staffId, LocalDate startDate, LocalDate endDate,
                                                           List<UUID> shiftIds, List<UUID> machineIds,
                                                           int page, int size) {
        return detailQuery.getStaffProducts(staffId, startDate, endDate, shiftIds, machineIds, page, size);
    }

    public PageResponse<ShiftProductRow> getShiftProducts(UUID shiftId, LocalDate startDate, LocalDate endDate,
                                                           List<UUID> staffIds, List<UUID> machineIds,
                                                           int page, int size) {
        return detailQuery.getShiftProducts(shiftId, startDate, endDate, staffIds, machineIds, page, size);
    }

    public PageResponse<DateProductRow> getDateProducts(LocalDate date,
                                                         List<UUID> shiftIds, List<UUID> staffIds,
                                                         List<UUID> machineIds, int page, int size) {
        return detailQuery.getDateProducts(date, shiftIds, staffIds, machineIds, page, size);
    }

    public PageResponse<ProductStopRow> getProductStops(UUID productId, int page, int size) {
        return detailQuery.getProductStops(productId, page, size);
    }

    public PageResponse<StopMachineRow> getStopMachines(UUID stopTypeId, String stopName, LocalDate startDate, LocalDate endDate,
                                                         List<UUID> shiftIds, List<UUID> staffIds,
                                                         List<UUID> machineIds, int page, int size) {
        return detailQuery.getStopMachines(stopTypeId, stopName, startDate, endDate, shiftIds, staffIds, machineIds, page, size);
    }

    public PageResponse<StopProductRow> getStopProducts(UUID workReportMachineId, UUID stopTypeId, String stopName,
                                                         int page, int size) {
        return detailQuery.getStopProducts(workReportMachineId, stopTypeId, stopName, page, size);
    }
}
