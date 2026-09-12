package com.pharm.pharmavigil_platform.validators.department;

import com.pharm.pharmavigil_platform.domain.Department;
import com.pharm.pharmavigil_platform.repository.DepartmentRepository;
import com.pharm.pharmavigil_platform.repository.MachineRepository;
import com.pharm.pharmavigil_platform.validators.Validator;
import com.pharm.pharmavigil_platform.validators.models.SystemViolation;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class DepartmentMachinesValidator implements Validator<Department> {

    private final MachineRepository machineRepository;
    private final DepartmentRepository departmentRepository;

    public DepartmentMachinesValidator(MachineRepository machineRepository, DepartmentRepository departmentRepository) {
        this.machineRepository = machineRepository;
        this.departmentRepository = departmentRepository;
    }

    @Override
    public Set<SystemViolation> validate(Department department) {
        Set<SystemViolation> violations = new LinkedHashSet<>();
        List<UUID> machineIds = department.getMachineIds();
        if (machineIds == null || machineIds.isEmpty()) {
            return violations;
        }

        Set<UUID> seen = new HashSet<>();
        for (UUID machineId : machineIds) {
            if (!seen.add(machineId)) {
                violations.add(new SystemViolation("machineIds", "department.machine.duplicate"));
                return violations;
            }
            if (!machineRepository.existsById(machineId)) {
                violations.add(new SystemViolation("machineIds", "department.machine.not.found"));
                return violations;
            }
            boolean alreadyAssigned = department.getId() == null
                    ? departmentRepository.existsByMachineId(machineId)
                    : departmentRepository.existsByMachineIdAndDepartmentIdNot(machineId, department.getId());
            if (alreadyAssigned) {
                violations.add(new SystemViolation("machineIds", "department.machine.already.assigned"));
                return violations;
            }
        }
        return violations;
    }
}
