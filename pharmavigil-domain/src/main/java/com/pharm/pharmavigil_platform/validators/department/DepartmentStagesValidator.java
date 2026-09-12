package com.pharm.pharmavigil_platform.validators.department;

import com.pharm.pharmavigil_platform.domain.Department;
import com.pharm.pharmavigil_platform.validators.Validator;
import com.pharm.pharmavigil_platform.validators.models.SystemViolation;

import java.util.*;

public class DepartmentStagesValidator implements Validator<Department> {

    @Override
    public Set<SystemViolation> validate(Department department) {
        Set<SystemViolation> violations = new LinkedHashSet<>();
        List<String> stages = department.getStages();
        if (stages == null || stages.isEmpty()) {
            return Collections.emptySet();
        }
        Set<String> seen = new HashSet<>();
        for (String stage : stages) {
            if (!seen.add(stage.toLowerCase())) {
                violations.add(new SystemViolation("stages", "department.stages.duplicate"));
                return violations;
            }
        }
        return violations;
    }
}
