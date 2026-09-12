package com.pharm.pharmavigil_platform.validators.user;

import com.pharm.pharmavigil_platform.domain.User;
import com.pharm.pharmavigil_platform.repository.UserRepository;
import com.pharm.pharmavigil_platform.validators.Validator;
import com.pharm.pharmavigil_platform.validators.models.SystemViolation;

import java.util.LinkedHashSet;
import java.util.Set;

public class UserEmailUniqueValidator implements Validator<User> {

    private final UserRepository repository;

    public UserEmailUniqueValidator(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public Set<SystemViolation> validate(User user) {
        Set<SystemViolation> violations = new LinkedHashSet<>();
        if (repository.existsByEmailIgnoreCase(user.getEmail())) {
            violations.add(new SystemViolation("email", "email.already.taken"));
        }
        return violations;
    }
}
