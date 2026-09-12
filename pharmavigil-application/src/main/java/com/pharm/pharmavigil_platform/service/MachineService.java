package com.pharm.pharmavigil_platform.service;

import com.pharm.pharmavigil_platform.domain.Machine;
import com.pharm.pharmavigil_platform.exception.ResourceNotFoundException;
import com.pharm.pharmavigil_platform.mapper.MachineMapper;
import com.pharm.pharmavigil_platform.repository.adapter.MachineRepositoryAdapter;
import com.pharm.pharmavigil_platform.repository.entities.MachineEntity;
import com.pharm.pharmavigil_platform.repository.specs.MachineSpec;
import com.pharm.pharmavigil_platform.resources.machine.CreateMachineRequest;
import com.pharm.pharmavigil_platform.resources.machine.MachineResponse;
import com.pharm.pharmavigil_platform.resources.machine.UpdateMachineRequest;
import com.pharm.pharmavigil_platform.usecases.machine.CreateMachineUseCase;
import com.pharm.pharmavigil_platform.usecases.machine.ToggleActiveMachineUseCase;
import com.pharm.pharmavigil_platform.usecases.machine.UpdateMachineUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MachineService {

    private final MachineRepositoryAdapter adapter;
    private final CreateMachineUseCase createMachineUseCase;
    private final UpdateMachineUseCase updateMachineUseCase;
    private final ToggleActiveMachineUseCase toggleActiveMachineUseCase;
    private final MachineMapper machineMapper;

    public Page<MachineResponse> getAll(MachineSpec spec, Pageable pageable) {
        return adapter.findAll(spec, pageable).map(machineMapper::toResponse);
    }

    public Page<MachineResponse> getActive(MachineSpec spec, Pageable pageable) {
        Specification<MachineEntity> combined = spec != null ? spec.and(isActive()) : Specification.where(isActive());
        return adapter.findAll(combined, pageable).map(machineMapper::toResponse);
    }

    public MachineResponse getById(UUID id) {
        return machineMapper.toResponse(findOrThrow(id));
    }

    @Transactional
    public MachineResponse create(CreateMachineRequest request) {
        Machine machine = machineMapper.toDomain(request);
        Machine created = createMachineUseCase.execute(machine);
        return machineMapper.toResponse(created);
    }

    @Transactional
    public MachineResponse update(UUID id, UpdateMachineRequest request) {
        Machine existing = findOrThrow(id);
        Machine machine = machineMapper.toDomain(request);
        machine.setId(id);
        machine.setActive(existing.isActive());
        machine.setCreatedAt(existing.getCreatedAt());
        machine.setCreatedBy(existing.getCreatedBy());
        Machine updated = updateMachineUseCase.execute(machine);
        return machineMapper.toResponse(updated);
    }

    @Transactional
    public MachineResponse toggleActive(UUID id) {
        Machine machine = findOrThrow(id);
        Machine saved = toggleActiveMachineUseCase.execute(machine);
        return machineMapper.toResponse(saved);
    }

    private Machine findOrThrow(UUID id) {
        return adapter.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Machine not found: " + id));
    }

    private Specification<MachineEntity> isActive() {
        return (root, query, cb) -> cb.isTrue(root.get("active"));
    }
}
