package com.pharm.pharmavigil_platform.validators.user;

import com.pharm.pharmavigil_platform.domain.User;
import com.pharm.pharmavigil_platform.validators.Validator;
import com.pharm.pharmavigil_platform.validators.models.SystemViolation;

import java.util.LinkedHashSet;
import java.util.Set;

public class UserNotDefaultAdminValidator implements Validator<User> {

    private final String defaultAdminEmail;

    public UserNotDefaultAdminValidator(String defaultAdminEmail) {
        this.defaultAdminEmail = defaultAdminEmail;
    }

    @Override
    public Set<SystemViolation> validate(User user) {
        Set<SystemViolation> violations = new LinkedHashSet<>();
        if (user.getEmail().equalsIgnoreCase(defaultAdminEmail)) {
            violations.add(new SystemViolation("user", "user.default.admin.cannot.be.modified"));
        }
        return violations;
    }
}
