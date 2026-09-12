package com.pharm.pharmavigil_platform.controller.supervisor;

import com.pharm.pharmavigil_platform.domain.UserRole;
import com.pharm.pharmavigil_platform.repository.specs.StopTypeSpec;
import com.pharm.pharmavigil_platform.resources.stoptype.CreateStopTypeRequest;
import com.pharm.pharmavigil_platform.resources.stoptype.StopTypeResponse;
import com.pharm.pharmavigil_platform.resources.stoptype.UpdateStopTypeRequest;
import com.pharm.pharmavigil_platform.security.RequiresRole;
import com.pharm.pharmavigil_platform.service.StopTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/supervisor/stop-types")
@RequiredArgsConstructor
public class StopTypeController {

    private final StopTypeService stopTypeService;

    @GetMapping
    @RequiresRole(UserRole.STOP_TYPES_MANAGER)
    public ResponseEntity<Page<StopTypeResponse>> getAll(StopTypeSpec spec, Pageable pageable) {
        return ResponseEntity.ok(stopTypeService.getAll(spec, pageable));
    }

    @GetMapping("/{id}")
    @RequiresRole(UserRole.STOP_TYPES_MANAGER)
    public ResponseEntity<StopTypeResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(stopTypeService.getById(id));
    }

    @PostMapping
    @RequiresRole(UserRole.STOP_TYPES_MANAGER)
    public ResponseEntity<StopTypeResponse> create(@RequestBody CreateStopTypeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stopTypeService.create(request));
    }

    @PutMapping("/{id}")
    @RequiresRole(UserRole.STOP_TYPES_MANAGER)
    public ResponseEntity<StopTypeResponse> update(@PathVariable UUID id, @RequestBody UpdateStopTypeRequest request) {
        return ResponseEntity.ok(stopTypeService.update(id, request));
    }

    @PatchMapping("/{id}/active")
    @RequiresRole(UserRole.STOP_TYPES_MANAGER)
    public ResponseEntity<StopTypeResponse> toggleActive(@PathVariable UUID id) {
        return ResponseEntity.ok(stopTypeService.toggleActive(id));
    }
}
