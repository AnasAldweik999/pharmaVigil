package com.pharm.pharmavigil_platform.controller.supervisor;

import com.pharm.pharmavigil_platform.domain.UserRole;
import com.pharm.pharmavigil_platform.repository.specs.SupervisorWorkReportFilterSpec;
import com.pharm.pharmavigil_platform.resources.supervisor.SupervisorWorkReportListResponse;
import com.pharm.pharmavigil_platform.resources.workreport.WorkReportResponse;
import com.pharm.pharmavigil_platform.security.RequiresRole;
import com.pharm.pharmavigil_platform.service.SupervisorWorkReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/supervisor/work-reports")
@RequiredArgsConstructor
public class SupervisorWorkReportController {

    private final SupervisorWorkReportService supervisorWorkReportService;

    @GetMapping
    @RequiresRole(UserRole.REPORTS_MANAGER)
    public ResponseEntity<Page<SupervisorWorkReportListResponse>> getAll(
            SupervisorWorkReportFilterSpec spec,
            Pageable pageable) {
        return ResponseEntity.ok(supervisorWorkReportService.getAll(spec, pageable));
    }

    @GetMapping("/{id}")
    @RequiresRole(UserRole.REPORTS_MANAGER)
    public ResponseEntity<WorkReportResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(supervisorWorkReportService.getById(id));
    }

    @DeleteMapping("/{id}")
    @RequiresRole(UserRole.REPORTS_MANAGER)
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        supervisorWorkReportService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
