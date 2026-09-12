package com.pharm.pharmavigil_platform.validators.models;

import com.pharm.pharmavigil_platform.validators.exceptions.SystemViolationException;

import java.util.Set;

public class ValidationResult {

    private final Set<SystemViolation> violations;

    public ValidationResult(Set<SystemViolation> violations) {
        this.violations = violations;
    }

    public void throwExceptionIfViolated() {
        if (!violations.isEmpty()) {
            throw new SystemViolationException(violations);
        }
    }
}
