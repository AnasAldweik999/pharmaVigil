package com.pharm.pharmavigil_platform.validators.department;

import com.pharm.pharmavigil_platform.domain.Department;
import com.pharm.pharmavigil_platform.repository.DepartmentRepository;
import com.pharm.pharmavigil_platform.validators.Validator;
import com.pharm.pharmavigil_platform.validators.models.SystemViolation;

import java.util.LinkedHashSet;
import java.util.Set;

public class DepartmentNameNotTakenByOthersValidator implements Validator<Department> {

    private final DepartmentRepository repository;

    public DepartmentNameNotTakenByOthersValidator(DepartmentRepository repository) {
        this.repository = repository;
    }

    @Override
    public Set<SystemViolation> validate(Department department) {
        Set<SystemViolation> violations = new LinkedHashSet<>();
        if (department.getName() != null && repository.existsByNameIgnoreCaseAndIdNot(department.getName(), department.getId())) {
            violations.add(new SystemViolation("name", "department.name.already.exists"));
        }
        return violations;
    }
}
