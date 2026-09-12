package com.pharm.pharmavigil_platform.validators;

import com.pharm.pharmavigil_platform.validators.models.SystemViolation;
import com.pharm.pharmavigil_platform.validators.models.ValidationResult;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ValidatorChain<T> {

    private final List<Validator<T>> validators;

    public ValidatorChain(List<Validator<T>> validators) {
        this.validators = validators;
    }

    public ValidationResult validate(T t) {
        Set<SystemViolation> all = new LinkedHashSet<>();
        for (Validator<T> validator : validators) {
            all.addAll(validator.validate(t));
        }
        return new ValidationResult(all);
    }
}
