package com.pharm.pharmavigil_platform.validators.department;

import com.pharm.pharmavigil_platform.domain.Department;
import com.pharm.pharmavigil_platform.domain.ExceptionalHolding;
import com.pharm.pharmavigil_platform.repository.ProductRepository;
import com.pharm.pharmavigil_platform.validators.Validator;
import com.pharm.pharmavigil_platform.validators.models.SystemViolation;

import java.util.*;

public class DepartmentHoldingValidator implements Validator<Department> {

    private final ProductRepository productRepository;

    public DepartmentHoldingValidator(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Set<SystemViolation> validate(Department department) {
        Set<SystemViolation> violations = new LinkedHashSet<>();

        if (department.getStandardHoldingTime() < 1) {
            violations.add(new SystemViolation("standardHoldingTime", "department.standard.holding.time.invalid"));
        }

        List<ExceptionalHolding> holdings = department.getExceptionalHoldings();
        if (holdings == null || holdings.isEmpty()) {
            return violations;
        }

        Set<UUID> seenProductIds = new HashSet<>();
        for (ExceptionalHolding holding : holdings) {
            if (holding.getProductId() == null) {
                violations.add(new SystemViolation("exceptionalHoldings", "department.holding.product.required"));
                return violations;
            }
            if (!seenProductIds.add(holding.getProductId())) {
                violations.add(new SystemViolation("exceptionalHoldings", "department.holding.product.duplicate"));
                return violations;
            }
            if (!productRepository.existsById(holding.getProductId())) {
                violations.add(new SystemViolation("exceptionalHoldings", "department.holding.product.not.found"));
                return violations;
            }
            if (holding.getHoldingTimeDays() < 1) {
                violations.add(new SystemViolation("exceptionalHoldings", "department.holding.time.invalid"));
                return violations;
            }
        }
        return violations;
    }
}
