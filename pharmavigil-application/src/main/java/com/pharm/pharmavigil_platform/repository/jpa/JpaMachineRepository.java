package com.pharm.pharmavigil_platform.repository.jpa;

import com.pharm.pharmavigil_platform.repository.entities.MachineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface JpaMachineRepository extends JpaRepository<MachineEntity, UUID>,
        JpaSpecificationExecutor<MachineEntity> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, UUID id);

    boolean existsByIdAndActiveTrue(UUID id);

    List<MachineEntity> findByDepartmentId(UUID departmentId);
}
