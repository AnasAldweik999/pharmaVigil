package com.pharm.pharmavigil_platform.validators.batch;

import com.pharm.pharmavigil_platform.domain.Batch;
import com.pharm.pharmavigil_platform.domain.BatchLogEntry;
import com.pharm.pharmavigil_platform.repository.DepartmentRepository;
import com.pharm.pharmavigil_platform.validators.Validator;
import com.pharm.pharmavigil_platform.validators.models.SystemViolation;

import java.util.LinkedHashSet;
import java.util.Set;

public class BatchLogEntryMachineDepartmentValidator implements Validator<Batch> {

    private final DepartmentRepository departmentRepository;

    public BatchLogEntryMachineDepartmentValidator(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    public Set<SystemViolation> validate(Batch batch) {
        Set<SystemViolation> violations = new LinkedHashSet<>();
        BatchLogEntry entry = batch.getEntries().get(batch.getEntries().size() - 1);
        if (entry.getDepartmentId() == null || entry.getMachineId() == null) {
            return violations;
        }
        if (!departmentRepository.existsByIdAndMachineId(entry.getDepartmentId(), entry.getMachineId())) {
            violations.add(new SystemViolation("machineId", "batch.machine.not.in.department"));
        }
        return violations;
    }
}
