package com.pharm.pharmavigil_platform.validators.user;

import com.pharm.pharmavigil_platform.domain.User;
import com.pharm.pharmavigil_platform.repository.DepartmentRepository;
import com.pharm.pharmavigil_platform.validators.Validator;
import com.pharm.pharmavigil_platform.validators.models.SystemViolation;

import java.util.LinkedHashSet;
import java.util.Set;

public class SupervisorAttachedToDepartmentValidator implements Validator<User> {

    private final DepartmentRepository departmentRepository;

    public SupervisorAttachedToDepartmentValidator(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    public Set<SystemViolation> validate(User user) {
        Set<SystemViolation> violations = new LinkedHashSet<>();
        if (departmentRepository.existsBySupervisorId(user.getId())) {
            violations.add(new SystemViolation("id", "user.attached.to.department"));
        }
        return violations;
    }
}
