package com.pharm.pharmavigil_platform.validators.exceptions;

import com.pharm.pharmavigil_platform.validators.models.SystemViolation;
import lombok.Getter;

import java.util.Set;

@Getter
public class SystemViolationException extends RuntimeException {

    private final Set<SystemViolation> violations;

    public SystemViolationException(Set<SystemViolation> violations) {
        super(violations.stream()
                .map(v -> "[" + v.field() + "] " + v.message())
                .reduce((a, b) -> a + ", " + b)
                .orElse("Validation failed"));
        this.violations = violations;
    }

    public SystemViolationException(String field, String message) {
        this(Set.of(new SystemViolation(field, message)));
    }

}
