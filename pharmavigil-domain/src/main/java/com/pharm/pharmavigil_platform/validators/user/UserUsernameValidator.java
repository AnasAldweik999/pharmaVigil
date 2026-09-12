package com.pharm.pharmavigil_platform.validators.user;

import com.pharm.pharmavigil_platform.domain.User;
import com.pharm.pharmavigil_platform.validators.Validator;
import com.pharm.pharmavigil_platform.validators.models.SystemViolation;

import java.util.LinkedHashSet;
import java.util.Set;

public class UserUsernameValidator implements Validator<User> {

    private static final java.util.regex.Pattern VALID_CHARS = java.util.regex.Pattern.compile("^[a-zA-Z0-9._]+$");

    @Override
    public Set<SystemViolation> validate(User user) {
        Set<SystemViolation> violations = new LinkedHashSet<>();
        String username = user.getUsername();
        if (username == null || username.isBlank()) {
            violations.add(new SystemViolation("username", "username.required"));
        } else if (username.length() > 255) {
            violations.add(new SystemViolation("username", "username.too.long"));
        } else if (!VALID_CHARS.matcher(username).matches()) {
            violations.add(new SystemViolation("username", "username.invalid.characters"));
        }
        return violations;
    }
}
