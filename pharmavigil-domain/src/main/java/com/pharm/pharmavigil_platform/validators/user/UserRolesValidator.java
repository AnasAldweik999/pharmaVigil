package com.pharm.pharmavigil_platform.validators.user;

import com.pharm.pharmavigil_platform.domain.User;
import com.pharm.pharmavigil_platform.domain.UserRole;
import com.pharm.pharmavigil_platform.repository.RoleDefinitionProvider;
import com.pharm.pharmavigil_platform.validators.Validator;
import com.pharm.pharmavigil_platform.validators.models.SystemViolation;

import java.util.LinkedHashSet;
import java.util.Set;

public class UserRolesValidator implements Validator<User> {

    private final RoleDefinitionProvider roleDefinitionProvider;

    public UserRolesValidator(RoleDefinitionProvider roleDefinitionProvider) {
        this.roleDefinitionProvider = roleDefinitionProvider;
    }

    @Override
    public Set<SystemViolation> validate(User user) {
        Set<SystemViolation> violations = new LinkedHashSet<>();
        if (user.getRoles() == null || user.getRoles().isEmpty()) return violations;

        Set<UserRole> allowed = roleDefinitionProvider.getAllowedRoles(user.getAccountType());
        if (!allowed.containsAll(user.getRoles())) {
            violations.add(new SystemViolation("roles", "roles.invalid.for.account.type"));
        }
        return violations;
    }
}
