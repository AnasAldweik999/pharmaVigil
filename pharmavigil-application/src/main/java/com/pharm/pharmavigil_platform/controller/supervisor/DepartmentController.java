package com.pharm.pharmavigil_platform.controller.supervisor;

import com.pharm.pharmavigil_platform.domain.UserRole;
import com.pharm.pharmavigil_platform.repository.specs.DepartmentSpec;
import com.pharm.pharmavigil_platform.resources.department.CreateDepartmentRequest;
import com.pharm.pharmavigil_platform.resources.department.DepartmentResponse;
import com.pharm.pharmavigil_platform.resources.department.DepartmentWithBatchesResponse;
import com.pharm.pharmavigil_platform.resources.department.UpdateDepartmentRequest;
import com.pharm.pharmavigil_platform.security.RequiresRole;
import com.pharm.pharmavigil_platform.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/supervisor/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping
    @RequiresRole({UserRole.DEPARTMENTS_MANAGER, UserRole.BATCH_TRACKING_MANAGER})
    public ResponseEntity<Page<DepartmentResponse>> getAll(DepartmentSpec spec, Pageable pageable) {
        return ResponseEntity.ok(departmentService.getAll(spec, pageable));
    }

    @GetMapping("/with-batches")
    @RequiresRole(UserRole.BATCH_TRACKING_MANAGER)
    public ResponseEntity<Page<DepartmentWithBatchesResponse>> getWithBatches(
            DepartmentSpec spec,
            @RequestParam(required = false) UUID productId,
            @RequestParam(required = false) String batchNo,
            @RequestParam(required = false, defaultValue = "all") String status,
            Pageable pageable) {
        return ResponseEntity.ok(departmentService.getAllWithBatches(spec, productId, batchNo, status, pageable));
    }

    @GetMapping("/allowed-units")
    @RequiresRole(UserRole.DEPARTMENTS_MANAGER)
    public ResponseEntity<List<String>> getAllowedUnits() {
        return ResponseEntity.ok(departmentService.getAllowedUnits());
    }

    @GetMapping("/{id}")
    @RequiresRole(UserRole.DEPARTMENTS_MANAGER)
    public ResponseEntity<DepartmentResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(departmentService.getById(id));
    }

    @PostMapping
    @RequiresRole(UserRole.DEPARTMENTS_MANAGER)
    public ResponseEntity<DepartmentResponse> create(@RequestBody CreateDepartmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(departmentService.create(request));
    }

    @PutMapping("/{id}")
    @RequiresRole(UserRole.DEPARTMENTS_MANAGER)
    public ResponseEntity<DepartmentResponse> update(@PathVariable UUID id,
                                                     @RequestBody UpdateDepartmentRequest request) {
        return ResponseEntity.ok(departmentService.update(id, request));
    }

    @PatchMapping("/{id}/active")
    @RequiresRole(UserRole.DEPARTMENTS_MANAGER)
    public ResponseEntity<DepartmentResponse> toggleActive(@PathVariable UUID id) {
        return ResponseEntity.ok(departmentService.toggleActive(id));
    }
}
