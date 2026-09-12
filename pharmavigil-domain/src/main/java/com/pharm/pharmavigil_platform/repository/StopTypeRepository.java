package com.pharm.pharmavigil_platform.repository;

import com.pharm.pharmavigil_platform.domain.StopType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StopTypeRepository {
    boolean existsByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndIdNot(String name, UUID id);
    boolean existsById(UUID id);
    Optional<StopType> findById(UUID id);
    List<StopType> findAll();
    StopType save(StopType stopType);
}
