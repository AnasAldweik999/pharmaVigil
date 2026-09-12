package com.pharm.pharmavigil_platform.repository.adapter;

import com.pharm.pharmavigil_platform.domain.Machine;
import com.pharm.pharmavigil_platform.mapper.MachineMapper;
import com.pharm.pharmavigil_platform.repository.MachineRepository;
import com.pharm.pharmavigil_platform.repository.entities.MachineEntity;
import com.pharm.pharmavigil_platform.repository.jpa.JpaMachineRepository;
import com.pharm.pharmavigil_platform.repository.listing.MachineListing;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MachineRepositoryAdapter implements MachineRepository, MachineListing {

    private final JpaMachineRepository jpaMachineRepository;
    private final MachineMapper machineMapper;

    @Override
    public boolean existsByNameIgnoreCase(String name) {
        return jpaMachineRepository.existsByNameIgnoreCase(name);
    }

    @Override
    public boolean existsByNameIgnoreCaseAndIdNot(String name, UUID id) {
        return jpaMachineRepository.existsByNameIgnoreCaseAndIdNot(name, id);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaMachineRepository.existsById(id);
    }

    @Override
    public boolean existsByIdAndActiveTrue(UUID id) {
        return jpaMachineRepository.existsByIdAndActiveTrue(id);
    }

    @Override
    public Optional<Machine> findById(UUID id) {
        return jpaMachineRepository.findById(id).map(machineMapper::toDomain);
    }

    @Override
    public Machine save(Machine machine) {
        // Load-then-patch rather than machineMapper.toEntity(machine) directly: the Machine domain
        // object has no `department` field (that's Machine's own live assignment to a Department,
        // unrelated to anything in the domain layer), so building a fresh entity from scratch would
        // null out an existing department link on every update.
        MachineEntity entity = machine.getId() != null
                ? jpaMachineRepository.findById(machine.getId()).orElseThrow()
                : new MachineEntity();
        entity.setName(machine.getName());
        entity.setActive(machine.isActive());
        entity.setLastUpdatedAt(machine.getLastUpdatedAt());
        entity.setLastUpdatedBy(machine.getLastUpdatedBy());
        if (machine.getId() == null) {
            entity.setCreatedBy(machine.getCreatedBy());
        }
        MachineEntity saved = jpaMachineRepository.save(entity);
        return machineMapper.toDomain(saved);
    }

    @Override
    public Page<Machine> findAll(Specification<MachineEntity> spec, Pageable pageable) {
        return jpaMachineRepository.findAll(spec, pageable).map(machineMapper::toDomain);
    }
}
