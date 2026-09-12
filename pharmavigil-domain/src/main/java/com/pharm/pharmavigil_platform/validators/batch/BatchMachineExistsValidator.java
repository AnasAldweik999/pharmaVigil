package com.pharm.pharmavigil_platform.validators.batch;

import com.pharm.pharmavigil_platform.domain.Batch;
import com.pharm.pharmavigil_platform.domain.BatchLogEntry;
import com.pharm.pharmavigil_platform.repository.MachineRepository;
import com.pharm.pharmavigil_platform.validators.Validator;
import com.pharm.pharmavigil_platform.validators.models.SystemViolation;

import java.util.LinkedHashSet;
import java.util.Set;

public class BatchMachineExistsValidator implements Validator<Batch> {

    private final MachineRepository machineRepository;

    public BatchMachineExistsValidator(MachineRepository machineRepository) {
        this.machineRepository = machineRepository;
    }

    @Override
    public Set<SystemViolation> validate(Batch batch) {
        Set<SystemViolation> violations = new LinkedHashSet<>();
        BatchLogEntry entry = batch.getEntries().get(batch.getEntries().size() - 1);
        if (entry.getMachineId() != null && !machineRepository.existsByIdAndActiveTrue(entry.getMachineId())) {
            violations.add(new SystemViolation("machineId", "batch.machine.not.found"));
        }
        return violations;
    }
}
