package com.pharm.pharmavigil_platform.controller.supervisor;

import com.pharm.pharmavigil_platform.domain.UserRole;
import com.pharm.pharmavigil_platform.repository.specs.MachineSpec;
import com.pharm.pharmavigil_platform.resources.machine.CreateMachineRequest;
import com.pharm.pharmavigil_platform.resources.machine.MachineResponse;
import com.pharm.pharmavigil_platform.resources.machine.UpdateMachineRequest;
import com.pharm.pharmavigil_platform.security.RequiresRole;
import com.pharm.pharmavigil_platform.service.MachineService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/supervisor/machines")
@RequiredArgsConstructor
public class MachineController {

    private final MachineService machineService;

    @GetMapping
    @RequiresRole({UserRole.MACHINE_MANAGER, UserRole.DASHBOARD_VIEWER, UserRole.DEPARTMENTS_MANAGER,  UserRole.BATCH_TRACKING_MANAGER})
    public ResponseEntity<Page<MachineResponse>> getAll(MachineSpec spec, Pageable pageable) {
        return ResponseEntity.ok(machineService.getAll(spec, pageable));
    }

    @GetMapping("/{id}")
    @RequiresRole(UserRole.MACHINE_MANAGER)
    public ResponseEntity<MachineResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(machineService.getById(id));
    }

    @PostMapping
    @RequiresRole(UserRole.MACHINE_MANAGER)
    public ResponseEntity<MachineResponse> create(@RequestBody CreateMachineRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(machineService.create(request));
    }

    @PutMapping("/{id}")
    @RequiresRole(UserRole.MACHINE_MANAGER)
    public ResponseEntity<MachineResponse> update(@PathVariable UUID id, @RequestBody UpdateMachineRequest request) {
        return ResponseEntity.ok(machineService.update(id, request));
    }

    @PatchMapping("/{id}/active")
    @RequiresRole(UserRole.MACHINE_MANAGER)
    public ResponseEntity<MachineResponse> toggleActive(@PathVariable UUID id) {
        return ResponseEntity.ok(machineService.toggleActive(id));
    }
}
