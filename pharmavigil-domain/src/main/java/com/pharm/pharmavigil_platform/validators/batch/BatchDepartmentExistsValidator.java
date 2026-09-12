package com.pharm.pharmavigil_platform.validators.batch;

import com.pharm.pharmavigil_platform.domain.Batch;
import com.pharm.pharmavigil_platform.domain.BatchLogEntry;
import com.pharm.pharmavigil_platform.repository.DepartmentRepository;
import com.pharm.pharmavigil_platform.validators.Validator;
import com.pharm.pharmavigil_platform.validators.models.SystemViolation;

import java.util.LinkedHashSet;
import java.util.Set;

public class BatchDepartmentExistsValidator implements Validator<Batch> {

    private final DepartmentRepository departmentRepository;

    public BatchDepartmentExistsValidator(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    public Set<SystemViolation> validate(Batch batch) {
        Set<SystemViolation> violations = new LinkedHashSet<>();
        BatchLogEntry entry = batch.getEntries().get(batch.getEntries().size() - 1);
        if (entry.getDepartmentId() == null) {
            return violations;
        }
        if (!departmentRepository.existsByIdAndActiveTrue(entry.getDepartmentId())) {
            violations.add(new SystemViolation("departmentId", "batch.department.not.found"));
        }
        return violations;
    }
}
