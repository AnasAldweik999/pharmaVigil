package com.pharm.pharmavigil_platform.validators.product;

import com.pharm.pharmavigil_platform.domain.Product;
import com.pharm.pharmavigil_platform.repository.ProductRepository;
import com.pharm.pharmavigil_platform.validators.Validator;
import com.pharm.pharmavigil_platform.validators.models.SystemViolation;

import java.util.LinkedHashSet;
import java.util.Set;

public class ProductExistsValidator implements Validator<Product> {

    private final ProductRepository repository;

    public ProductExistsValidator(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public Set<SystemViolation> validate(Product product) {
        Set<SystemViolation> violations = new LinkedHashSet<>();
        if (!repository.existsById(product.getId())) {
            violations.add(new SystemViolation("id", "product.not.found"));
        }
        return violations;
    }
}
