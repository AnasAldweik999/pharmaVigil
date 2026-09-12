package com.pharm.pharmavigil_platform.validators.department;

import com.pharm.pharmavigil_platform.domain.Department;
import com.pharm.pharmavigil_platform.repository.DepartmentRepository;
import com.pharm.pharmavigil_platform.validators.Validator;
import com.pharm.pharmavigil_platform.validators.models.SystemViolation;

import java.util.LinkedHashSet;
import java.util.Set;

public class DepartmentExistsValidator implements Validator<Department> {

    private final DepartmentRepository repository;

    public DepartmentExistsValidator(DepartmentRepository repository) {
        this.repository = repository;
    }

    @Override
    public Set<SystemViolation> validate(Department department) {
        Set<SystemViolation> violations = new LinkedHashSet<>();
        if (!repository.existsById(department.getId())) {
            violations.add(new SystemViolation("id", "department.not.found"));
        }
        return violations;
    }
}
