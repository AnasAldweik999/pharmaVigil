package com.pharm.pharmavigil_platform.validators.product;

import com.pharm.pharmavigil_platform.domain.Product;
import com.pharm.pharmavigil_platform.validators.Validator;
import com.pharm.pharmavigil_platform.validators.models.SystemViolation;

import java.util.LinkedHashSet;
import java.util.Set;

public class ProductNameValidator implements Validator<Product> {

    @Override
    public Set<SystemViolation> validate(Product product) {
        Set<SystemViolation> violations = new LinkedHashSet<>();
        String name = product.getName();
        if (name == null || name.isBlank()) {
            violations.add(new SystemViolation("name", "name.required"));
        } else if (name.length() < 2 || name.length() > 100) {
            violations.add(new SystemViolation("name", "name.length.invalid"));
        }
        return violations;
    }
}
