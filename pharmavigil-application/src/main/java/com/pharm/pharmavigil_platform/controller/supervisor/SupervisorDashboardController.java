package com.pharm.pharmavigil_platform.controller.supervisor;

import com.pharm.pharmavigil_platform.domain.SummaryGroupBy;
import com.pharm.pharmavigil_platform.domain.UserRole;
import com.pharm.pharmavigil_platform.resources.supervisor.dashboard.*;
import com.pharm.pharmavigil_platform.security.RequiresRole;
import com.pharm.pharmavigil_platform.service.SupervisorDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/supervisor")
@RequiredArgsConstructor
public class SupervisorDashboardController {

    //todo: dashboard needs to be refactored

    private final SupervisorDashboardService service;

    @GetMapping("/summary")
    @RequiresRole(UserRole.DASHBOARD_VIEWER)
    public ResponseEntity<DashboardSummaryResponse> getSummary(
            @RequestParam SummaryGroupBy groupBy,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) List<UUID> shiftIds,
            @RequestParam(required = false) List<UUID> staffIds,
            @RequestParam(required = false) List<UUID> machineIds) {

        return ResponseEntity.ok(
                service.getSummary(groupBy, startDate, endDate, shiftIds, staffIds, machineIds)
        );
    }

    @GetMapping("/grouped")
    @RequiresRole(UserRole.DASHBOARD_VIEWER)
    public ResponseEntity<PageResponse<?>> getGrouped(
            @RequestParam SummaryGroupBy groupBy,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) List<UUID> shiftIds,
            @RequestParam(required = false) List<UUID> staffIds,
            @RequestParam(required = false) List<UUID> machineIds,
            Pageable pageable) {

        return ResponseEntity.ok(
                service.getGroupedData(groupBy, startDate, endDate, shiftIds, staffIds, machineIds,
                        pageable.getPageNumber(), pageable.getPageSize())
        );
    }

    @GetMapping("/machine/products")
    @RequiresRole(UserRole.DASHBOARD_VIEWER)
    public ResponseEntity<PageResponse<MachineProductRow>> getMachineProducts(
            @RequestParam UUID machineId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) List<UUID> shiftIds,
            @RequestParam(required = false) List<UUID> staffIds,
            Pageable pageable) {

        return ResponseEntity.ok(
                service.getMachineProducts(machineId, startDate, endDate, shiftIds, staffIds,
                        pageable.getPageNumber(), pageable.getPageSize())
        );
    }

    @GetMapping("/staff/products")
    @RequiresRole(UserRole.DASHBOARD_VIEWER)
    public ResponseEntity<PageResponse<StaffProductRow>> getStaffProducts(
            @RequestParam UUID staffId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) List<UUID> shiftIds,
            @RequestParam(required = false) List<UUID> machineIds,
            Pageable pageable) {

        return ResponseEntity.ok(
                service.getStaffProducts(staffId, startDate, endDate, shiftIds, machineIds,
                        pageable.getPageNumber(), pageable.getPageSize())
        );
    }

    @GetMapping("/shift/products")
    @RequiresRole(UserRole.DASHBOARD_VIEWER)
    public ResponseEntity<PageResponse<ShiftProductRow>> getShiftProducts(
            @RequestParam UUID shiftId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) List<UUID> staffIds,
            @RequestParam(required = false) List<UUID> machineIds,
            Pageable pageable) {

        return ResponseEntity.ok(
                service.getShiftProducts(shiftId, startDate, endDate, staffIds, machineIds,
                        pageable.getPageNumber(), pageable.getPageSize())
        );
    }

    @GetMapping("/date/products")
    @RequiresRole(UserRole.DASHBOARD_VIEWER)
    public ResponseEntity<PageResponse<DateProductRow>> getDateProducts(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) List<UUID> shiftIds,
            @RequestParam(required = false) List<UUID> staffIds,
            @RequestParam(required = false) List<UUID> machineIds,
            Pageable pageable) {

        return ResponseEntity.ok(
                service.getDateProducts(date, shiftIds, staffIds, machineIds,
                        pageable.getPageNumber(), pageable.getPageSize())
        );
    }

    @GetMapping("/products/stops")
    @RequiresRole(UserRole.DASHBOARD_VIEWER)
    public ResponseEntity<PageResponse<ProductStopRow>> getProductStops(
            @RequestParam UUID productId,
            Pageable pageable) {
        return ResponseEntity.ok(
                service.getProductStops(productId, pageable.getPageNumber(), pageable.getPageSize())
        );
    }

    @GetMapping("/stop/machines")
    @RequiresRole(UserRole.DASHBOARD_VIEWER)
    public ResponseEntity<PageResponse<StopMachineRow>> getStopMachines(
            @RequestParam(required = false) UUID stopTypeId,
            @RequestParam(required = false) String stopName,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) List<UUID> shiftIds,
            @RequestParam(required = false) List<UUID> staffIds,
            @RequestParam(required = false) List<UUID> machineIds,
            Pageable pageable) {
        requireExactlyOneStopSelector(stopTypeId, stopName);
        return ResponseEntity.ok(
                service.getStopMachines(stopTypeId, stopName, startDate, endDate, shiftIds, staffIds, machineIds,
                        pageable.getPageNumber(), pageable.getPageSize())
        );
    }

    @GetMapping("/stop/products")
    @RequiresRole(UserRole.DASHBOARD_VIEWER)
    public ResponseEntity<PageResponse<StopProductRow>> getStopProducts(
            @RequestParam UUID workReportMachineId,
            @RequestParam(required = false) UUID stopTypeId,
            @RequestParam(required = false) String stopName,
            Pageable pageable) {
        requireExactlyOneStopSelector(stopTypeId, stopName);
        return ResponseEntity.ok(
                service.getStopProducts(workReportMachineId, stopTypeId, stopName,
                        pageable.getPageNumber(), pageable.getPageSize())
        );
    }

    // stop-type selection is ID-based going forward; stopName is kept only so links generated
    // for legacy pre-refactor stop entries (whose stop_type_id is null) still resolve.
    private void requireExactlyOneStopSelector(UUID stopTypeId, String stopName) {
        boolean hasId = stopTypeId != null;
        boolean hasName = stopName != null && !stopName.isBlank();
        if (hasId == hasName) {
            throw new IllegalArgumentException("Provide exactly one of stopTypeId or stopName");
        }
    }
}
