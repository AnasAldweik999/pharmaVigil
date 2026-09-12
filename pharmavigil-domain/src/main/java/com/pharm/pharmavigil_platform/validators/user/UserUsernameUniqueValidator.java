package com.pharm.pharmavigil_platform.validators.user;

import com.pharm.pharmavigil_platform.domain.User;
import com.pharm.pharmavigil_platform.repository.UserRepository;
import com.pharm.pharmavigil_platform.validators.Validator;
import com.pharm.pharmavigil_platform.validators.models.SystemViolation;

import java.util.LinkedHashSet;
import java.util.Set;

public class UserUsernameUniqueValidator implements Validator<User> {

    private final UserRepository repository;

    public UserUsernameUniqueValidator(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public Set<SystemViolation> validate(User user) {
        Set<SystemViolation> violations = new LinkedHashSet<>();
        if (user.getUsername() != null && repository.existsByUsername(user.getUsername())) {
            violations.add(new SystemViolation("username", "username.already.taken"));
        }
        return violations;
    }
}
