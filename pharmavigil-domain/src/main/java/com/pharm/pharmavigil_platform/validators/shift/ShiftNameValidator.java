package com.pharm.pharmavigil_platform.validators.shift;

import com.pharm.pharmavigil_platform.domain.Shift;
import com.pharm.pharmavigil_platform.validators.Validator;
import com.pharm.pharmavigil_platform.validators.models.SystemViolation;

import java.util.LinkedHashSet;
import java.util.Set;

public class ShiftNameValidator implements Validator<Shift> {

    @Override
    public Set<SystemViolation> validate(Shift shift) {
        Set<SystemViolation> violations = new LinkedHashSet<>();
        String name = shift.getName();
        if (name == null || name.isBlank()) {
            violations.add(new SystemViolation("name", "name.required"));
        }
        return violations;
    }
}
