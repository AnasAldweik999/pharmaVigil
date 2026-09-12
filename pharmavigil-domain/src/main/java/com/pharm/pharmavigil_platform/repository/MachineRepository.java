package com.pharm.pharmavigil_platform.repository;

import com.pharm.pharmavigil_platform.domain.Machine;

import java.util.Optional;
import java.util.UUID;

public interface MachineRepository {
    boolean existsByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndIdNot(String name, UUID id);
    boolean existsById(UUID id);
    boolean existsByIdAndActiveTrue(UUID id);
    Optional<Machine> findById(UUID id);
    Machine save(Machine machine);
}
