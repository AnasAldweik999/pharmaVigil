package com.pharm.pharmavigil_platform.validators.user;

import com.pharm.pharmavigil_platform.domain.User;
import com.pharm.pharmavigil_platform.validators.Validator;
import com.pharm.pharmavigil_platform.validators.models.SystemViolation;

import java.util.LinkedHashSet;
import java.util.Set;

public class UserFullNameValidator implements Validator<User> {

    @Override
    public Set<SystemViolation> validate(User user) {
        Set<SystemViolation> violations = new LinkedHashSet<>();
        String name = user.getName();
        if (name == null || name.isBlank()) {
            violations.add(new SystemViolation("name", "name.required"));
        } else if (name.trim().length() < 3) {
            violations.add(new SystemViolation("name", "name.too.short"));
        }
        return violations;
    }
}
