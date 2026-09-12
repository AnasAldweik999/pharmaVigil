package com.pharm.pharmavigil_platform.validators.batch;

import com.pharm.pharmavigil_platform.domain.Batch;
import com.pharm.pharmavigil_platform.domain.BatchLogEntry;
import com.pharm.pharmavigil_platform.domain.Department;
import com.pharm.pharmavigil_platform.repository.DepartmentRepository;
import com.pharm.pharmavigil_platform.validators.Validator;
import com.pharm.pharmavigil_platform.validators.models.SystemViolation;

import java.util.LinkedHashSet;
import java.util.Set;

public class BatchLogEntryDuplicateMachineValidator implements Validator<Batch> {

    private final DepartmentRepository departmentRepository;

    public BatchLogEntryDuplicateMachineValidator(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    public Set<SystemViolation> validate(Batch batch) {
        Set<SystemViolation> violations = new LinkedHashSet<>();
        BatchLogEntry entry = batch.getEntries().get(batch.getEntries().size() - 1);
        if (entry.isCompleted() || entry.getMachineId() == null || entry.getDepartmentId() == null) {
            return violations;
        }
        boolean terminalDepartment = departmentRepository.findById(entry.getDepartmentId())
                .map(Department::isTerminalDepartment)
                .orElse(false);
        if (terminalDepartment) {
            return violations;
        }
        boolean alreadyUsed = batch.getEntries().stream()
                .filter(other -> other != entry)
                .anyMatch(other -> entry.getMachineId().equals(other.getMachineId())
                        && entry.getDepartmentId().equals(other.getDepartmentId()));
        if (alreadyUsed) {
            violations.add(new SystemViolation("machineId", "batch.machine.already.logged"));
        }
        return violations;
    }
}
