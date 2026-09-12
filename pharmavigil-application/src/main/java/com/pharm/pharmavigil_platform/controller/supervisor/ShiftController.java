package com.pharm.pharmavigil_platform.controller.supervisor;

import com.pharm.pharmavigil_platform.domain.UserRole;
import com.pharm.pharmavigil_platform.repository.specs.ShiftSpec;
import com.pharm.pharmavigil_platform.resources.shift.CreateShiftRequest;
import com.pharm.pharmavigil_platform.resources.shift.ShiftResponse;
import com.pharm.pharmavigil_platform.resources.shift.UpdateShiftRequest;
import com.pharm.pharmavigil_platform.security.RequiresRole;
import com.pharm.pharmavigil_platform.service.ShiftService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/supervisor/shifts")
@RequiredArgsConstructor
public class ShiftController {

    private final ShiftService shiftService;

    @GetMapping
    @RequiresRole({UserRole.SHIFT_MANAGER, UserRole.DASHBOARD_VIEWER})
    public ResponseEntity<Page<ShiftResponse>> getAll(ShiftSpec spec, Pageable pageable) {
        return ResponseEntity.ok(shiftService.getAll(spec, pageable));
    }

    @GetMapping("/{id}")
    @RequiresRole(UserRole.SHIFT_MANAGER)
    public ResponseEntity<ShiftResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(shiftService.getById(id));
    }

    @PostMapping
    @RequiresRole(UserRole.SHIFT_MANAGER)
    public ResponseEntity<ShiftResponse> create(@RequestBody CreateShiftRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(shiftService.create(request));
    }

    @PutMapping("/{id}")
    @RequiresRole(UserRole.SHIFT_MANAGER)
    public ResponseEntity<ShiftResponse> update(@PathVariable UUID id, @RequestBody UpdateShiftRequest request) {
        return ResponseEntity.ok(shiftService.update(id, request));
    }

    @PatchMapping("/{id}/active")
    @RequiresRole(UserRole.SHIFT_MANAGER)
    public ResponseEntity<ShiftResponse> toggleActive(@PathVariable UUID id) {
        return ResponseEntity.ok(shiftService.toggleActive(id));
    }
}
