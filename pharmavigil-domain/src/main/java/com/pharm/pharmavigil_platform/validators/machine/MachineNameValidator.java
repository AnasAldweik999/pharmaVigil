package com.pharm.pharmavigil_platform.validators.machine;

import com.pharm.pharmavigil_platform.domain.Machine;
import com.pharm.pharmavigil_platform.validators.Validator;
import com.pharm.pharmavigil_platform.validators.models.SystemViolation;

import java.util.LinkedHashSet;
import java.util.Set;

public class MachineNameValidator implements Validator<Machine> {

    @Override
    public Set<SystemViolation> validate(Machine machine) {
        Set<SystemViolation> violations = new LinkedHashSet<>();
        String name = machine.getName();
        if (name == null || name.isBlank()) {
            violations.add(new SystemViolation("name", "name.required"));
        } else if (name.length() < 2 || name.length() > 100) {
            violations.add(new SystemViolation("name", "name.length.invalid"));
        }
        return violations;
    }
}
