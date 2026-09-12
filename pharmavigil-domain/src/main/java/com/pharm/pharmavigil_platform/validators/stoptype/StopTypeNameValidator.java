package com.pharm.pharmavigil_platform.validators.stoptype;

import com.pharm.pharmavigil_platform.domain.StopType;
import com.pharm.pharmavigil_platform.validators.Validator;
import com.pharm.pharmavigil_platform.validators.models.SystemViolation;

import java.util.LinkedHashSet;
import java.util.Set;

public class StopTypeNameValidator implements Validator<StopType> {

    @Override
    public Set<SystemViolation> validate(StopType stopType) {
        Set<SystemViolation> violations = new LinkedHashSet<>();
        String name = stopType.getName();
        if (name == null || name.isBlank()) {
            violations.add(new SystemViolation("name", "name.required"));
        } else if (name.length() < 2 || name.length() > 100) {
            violations.add(new SystemViolation("name", "name.length.invalid"));
        }
        return violations;
    }
}
