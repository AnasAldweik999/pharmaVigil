package com.pharm.pharmavigil_platform.validators.user;

import com.pharm.pharmavigil_platform.domain.User;
import com.pharm.pharmavigil_platform.domain.UserStatus;
import com.pharm.pharmavigil_platform.validators.Validator;
import com.pharm.pharmavigil_platform.validators.models.SystemViolation;

import java.util.LinkedHashSet;
import java.util.Set;

public class UserStatusNotPendingValidator implements Validator<User> {

    @Override
    public Set<SystemViolation> validate(User user) {
        Set<SystemViolation> violations = new LinkedHashSet<>();
        if (user.getStatus() == UserStatus.PENDING_EMAIL_VERIFICATION) {
            violations.add(new SystemViolation("account", "account.status.pending.verification"));
        }
        return violations;
    }
}
