package com.pharm.pharmavigil_platform.validators.shift;

import com.pharm.pharmavigil_platform.domain.Shift;
import com.pharm.pharmavigil_platform.repository.ShiftRepository;
import com.pharm.pharmavigil_platform.validators.Validator;
import com.pharm.pharmavigil_platform.validators.models.SystemViolation;

import java.util.LinkedHashSet;
import java.util.Set;

public class ShiftUniquenessValidator implements Validator<Shift> {

    private final ShiftRepository repository;

    public ShiftUniquenessValidator(ShiftRepository repository) {
        this.repository = repository;
    }

    @Override
    public Set<SystemViolation> validate(Shift shift) {
        Set<SystemViolation> violations = new LinkedHashSet<>();
        if (repository.existsByNameIgnoreCase(shift.getName())) {
            violations.add(new SystemViolation("name", "shift.name.already.exists"));
        }
        return violations;
    }
}
