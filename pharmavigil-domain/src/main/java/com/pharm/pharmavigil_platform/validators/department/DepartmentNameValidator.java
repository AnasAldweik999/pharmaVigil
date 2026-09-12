package com.pharm.pharmavigil_platform.validators.department;

import com.pharm.pharmavigil_platform.domain.Department;
import com.pharm.pharmavigil_platform.validators.Validator;
import com.pharm.pharmavigil_platform.validators.models.SystemViolation;

import java.util.LinkedHashSet;
import java.util.Set;

public class DepartmentNameValidator implements Validator<Department> {

    @Override
    public Set<SystemViolation> validate(Department department) {
        Set<SystemViolation> violations = new LinkedHashSet<>();
        String name = department.getName();
        if (name == null || name.isBlank()) {
            violations.add(new SystemViolation("name", "department.name.required"));
        } else if (name.length() > 255) {
            violations.add(new SystemViolation("name", "department.name.length.invalid"));
        }
        return violations;
    }
}
