package com.pharm.pharmavigil_platform.controller.staff;

import com.pharm.pharmavigil_platform.domain.UserRole;
import com.pharm.pharmavigil_platform.repository.specs.WorkReportFilterSpec;
import com.pharm.pharmavigil_platform.resources.workreport.CreateWorkReportRequest;
import com.pharm.pharmavigil_platform.resources.workreport.WorkReportResponse;
import com.pharm.pharmavigil_platform.security.RequiresRole;
import com.pharm.pharmavigil_platform.service.WorkReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/staff/work-reports")
@RequiredArgsConstructor
public class WorkReportController {

    private final WorkReportService workReportService;

    @PostMapping
    @RequiresRole(UserRole.FIELD_REPORTER)
    public ResponseEntity<WorkReportResponse> create(@RequestBody CreateWorkReportRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(workReportService.create(request));
    }

    @GetMapping
    @RequiresRole(UserRole.FIELD_REPORTER)
    public ResponseEntity<Page<WorkReportResponse>> getAll(WorkReportFilterSpec spec, Pageable pageable) {
        return ResponseEntity.ok(workReportService.getAllStaffReports(spec, pageable));
    }

    @GetMapping("/{id}")
    @RequiresRole(UserRole.FIELD_REPORTER)
    public ResponseEntity<WorkReportResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(workReportService.getById(id));
    }
}
