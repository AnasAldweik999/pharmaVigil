package com.pharm.pharmavigil_platform.validators.machine;

import com.pharm.pharmavigil_platform.domain.Machine;
import com.pharm.pharmavigil_platform.repository.MachineRepository;
import com.pharm.pharmavigil_platform.validators.Validator;
import com.pharm.pharmavigil_platform.validators.models.SystemViolation;

import java.util.LinkedHashSet;
import java.util.Set;

public class MachineUniquenessValidator implements Validator<Machine> {

    private final MachineRepository repository;

    public MachineUniquenessValidator(MachineRepository repository) {
        this.repository = repository;
    }

    @Override
    public Set<SystemViolation> validate(Machine machine) {
        Set<SystemViolation> violations = new LinkedHashSet<>();
        if (repository.existsByNameIgnoreCase(machine.getName())) {
            violations.add(new SystemViolation("name", "machine.name.already.exists"));
        }
        return violations;
    }
}
