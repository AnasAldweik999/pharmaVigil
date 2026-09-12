package com.pharm.pharmavigil_platform.validators.department;

import com.pharm.pharmavigil_platform.domain.AccountType;
import com.pharm.pharmavigil_platform.domain.Department;
import com.pharm.pharmavigil_platform.domain.User;
import com.pharm.pharmavigil_platform.domain.UserStatus;
import com.pharm.pharmavigil_platform.repository.UserRepository;
import com.pharm.pharmavigil_platform.validators.Validator;
import com.pharm.pharmavigil_platform.validators.models.SystemViolation;

import java.util.*;

public class DepartmentSupervisorsValidator implements Validator<Department> {

    private final UserRepository userRepository;

    public DepartmentSupervisorsValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Set<SystemViolation> validate(Department department) {
        Set<SystemViolation> violations = new LinkedHashSet<>();
        List<UUID> supervisorIds = department.getSupervisorIds();
        if (supervisorIds == null || supervisorIds.isEmpty()) {
            return violations;
        }
        Set<UUID> seen = new HashSet<>();
        for (UUID supervisorId : supervisorIds) {
            if (!seen.add(supervisorId)) {
                violations.add(new SystemViolation("supervisorIds", "department.supervisors.duplicate"));
                return violations;
            }
            Optional<User> userOpt = userRepository.findById(supervisorId);
            if (userOpt.isEmpty()) {
                violations.add(new SystemViolation("supervisorIds", "department.supervisor.not.found"));
                return violations;
            }
            User user = userOpt.get();
            if (user.getAccountType() != AccountType.SUPERVISOR) {
                violations.add(new SystemViolation("supervisorIds", "department.user.not.supervisor"));
                return violations;
            }
            if (user.getStatus() != UserStatus.ACTIVE) {
                violations.add(new SystemViolation("supervisorIds", "department.supervisor.not.active"));
                return violations;
            }
        }
        return violations;
    }
}
