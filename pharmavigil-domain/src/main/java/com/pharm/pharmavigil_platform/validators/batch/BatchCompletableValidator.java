package com.pharm.pharmavigil_platform.validators.batch;

import com.pharm.pharmavigil_platform.domain.Batch;
import com.pharm.pharmavigil_platform.domain.BatchLogEntry;
import com.pharm.pharmavigil_platform.domain.Department;
import com.pharm.pharmavigil_platform.repository.DepartmentRepository;
import com.pharm.pharmavigil_platform.validators.Validator;
import com.pharm.pharmavigil_platform.validators.models.SystemViolation;

import java.util.LinkedHashSet;
import java.util.Set;

public class BatchCompletableValidator implements Validator<Batch> {

    private final DepartmentRepository departmentRepository;

    public BatchCompletableValidator(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    public Set<SystemViolation> validate(Batch batch) {
        Set<SystemViolation> violations = new LinkedHashSet<>();
        BatchLogEntry entry = batch.getEntries().get(batch.getEntries().size() - 1);
        if (!entry.isCompleted()) {
            return violations;
        }
        boolean currentDepartmentIsTerminal = batch.getId() != null && batch.getCurrentDepartmentId() != null
                && departmentRepository.findById(batch.getCurrentDepartmentId())
                        .map(Department::isTerminalDepartment)
                        .orElse(false);
        if (!currentDepartmentIsTerminal) {
            violations.add(new SystemViolation("completed", "batch.not.completable"));
        }
        return violations;
    }
}
