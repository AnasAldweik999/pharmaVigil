package com.pharm.pharmavigil_platform.repository;

import com.pharm.pharmavigil_platform.domain.Shift;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShiftRepository {
    boolean existsByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndIdNot(String name, UUID id);
    boolean existsById(UUID id);
    boolean existsByIdAndActiveTrue(UUID id);
    Optional<Shift> findById(UUID id);
    List<Shift> findAll();
    Shift save(Shift shift);
}
