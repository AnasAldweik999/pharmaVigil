package com.pharm.pharmavigil_platform.validators.department;

import com.pharm.pharmavigil_platform.domain.Department;
import com.pharm.pharmavigil_platform.repository.AllowedUnitsProvider;
import com.pharm.pharmavigil_platform.validators.Validator;
import com.pharm.pharmavigil_platform.validators.models.SystemViolation;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class DepartmentUnitsValidator implements Validator<Department> {

    private final AllowedUnitsProvider allowedUnitsProvider;

    public DepartmentUnitsValidator(AllowedUnitsProvider allowedUnitsProvider) {
        this.allowedUnitsProvider = allowedUnitsProvider;
    }

    @Override
    public Set<SystemViolation> validate(Department department) {
        Set<SystemViolation> violations = new LinkedHashSet<>();
        if (!department.isHasOutputs()) {
            return violations;
        }
        List<String> units = department.getUnits();
        if (units == null || units.isEmpty()) {
            violations.add(new SystemViolation("units", "department.units.required"));
            return violations;
        }
        List<String> allowed = allowedUnitsProvider.getAllowedUnits();
        for (String unit : units) {
            boolean valid = allowed.stream().anyMatch(a -> a.equalsIgnoreCase(unit));
            if (!valid) {
                violations.add(new SystemViolation("units", "department.units.invalid"));
                break;
            }
        }
        return violations;
    }
}
