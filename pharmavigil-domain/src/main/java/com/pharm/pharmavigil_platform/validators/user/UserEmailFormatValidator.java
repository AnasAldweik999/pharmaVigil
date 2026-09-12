package com.pharm.pharmavigil_platform.validators.user;

import com.pharm.pharmavigil_platform.domain.User;
import com.pharm.pharmavigil_platform.validators.Validator;
import com.pharm.pharmavigil_platform.validators.models.SystemViolation;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class UserEmailFormatValidator implements Validator<User> {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    @Override
    public Set<SystemViolation> validate(User user) {
        Set<SystemViolation> violations = new LinkedHashSet<>();
        String email = user.getEmail();
        if (email == null || email.isBlank()) {
            violations.add(new SystemViolation("email", "email.required"));
        } else if (!EMAIL_PATTERN.matcher(email).matches()) {
            violations.add(new SystemViolation("email", "email.format.invalid"));
        }
        return violations;
    }
}
