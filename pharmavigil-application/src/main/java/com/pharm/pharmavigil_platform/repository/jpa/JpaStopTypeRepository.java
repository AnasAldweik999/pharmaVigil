package com.pharm.pharmavigil_platform.repository.jpa;

import com.pharm.pharmavigil_platform.repository.entities.StopTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface JpaStopTypeRepository extends JpaRepository<StopTypeEntity, UUID>,
        JpaSpecificationExecutor<StopTypeEntity> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, UUID id);
}
