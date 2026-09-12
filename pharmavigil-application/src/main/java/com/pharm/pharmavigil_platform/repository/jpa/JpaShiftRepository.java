package com.pharm.pharmavigil_platform.repository.jpa;

import com.pharm.pharmavigil_platform.repository.entities.ShiftEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface JpaShiftRepository extends JpaRepository<ShiftEntity, UUID>,
        JpaSpecificationExecutor<ShiftEntity> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, UUID id);

    boolean existsByIdAndActiveTrue(UUID id);
}
