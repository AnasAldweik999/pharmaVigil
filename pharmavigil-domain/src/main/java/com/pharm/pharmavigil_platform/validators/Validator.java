package com.pharm.pharmavigil_platform.validators;

import com.pharm.pharmavigil_platform.validators.models.SystemViolation;

import java.util.Set;

public interface Validator<T> {
    Set<SystemViolation> validate(T t);
}
