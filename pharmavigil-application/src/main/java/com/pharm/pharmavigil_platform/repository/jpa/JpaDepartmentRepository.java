package com.pharm.pharmavigil_platform.repository.jpa;

import com.pharm.pharmavigil_platform.repository.entities.DepartmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface JpaDepartmentRepository
        extends JpaRepository<DepartmentEntity, UUID>, JpaSpecificationExecutor<DepartmentEntity> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, UUID id);

    boolean existsByIdAndActiveTrue(UUID id);

    boolean existsByMachines_Id(UUID machineId);

    boolean existsByMachines_IdAndIdNot(UUID machineId, UUID departmentId);

    boolean existsByIdAndMachines_Id(UUID id, UUID machineId);

    boolean existsBySupervisors_Id(UUID userId);
}
