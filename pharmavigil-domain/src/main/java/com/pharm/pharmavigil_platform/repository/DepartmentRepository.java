package com.pharm.pharmavigil_platform.repository;

import com.pharm.pharmavigil_platform.domain.Department;

import java.util.Optional;
import java.util.UUID;

public interface DepartmentRepository {
    boolean existsByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndIdNot(String name, UUID id);
    boolean existsById(UUID id);
    boolean existsByIdAndActiveTrue(UUID id);
    Optional<Department> findById(UUID id);
    Department save(Department department);
    boolean existsByMachineId(UUID machineId);
    boolean existsByMachineIdAndDepartmentIdNot(UUID machineId, UUID departmentId);
    boolean existsByIdAndMachineId(UUID departmentId, UUID machineId);
    boolean existsBySupervisorId(UUID userId);
}
