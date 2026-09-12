package com.pharm.pharmavigil_platform.validators.product;

import com.pharm.pharmavigil_platform.domain.Product;
import com.pharm.pharmavigil_platform.repository.ProductRepository;
import com.pharm.pharmavigil_platform.validators.Validator;
import com.pharm.pharmavigil_platform.validators.models.SystemViolation;

import java.util.LinkedHashSet;
import java.util.Set;

public class ProductUniquenessValidator implements Validator<Product> {

    private final ProductRepository repository;

    public ProductUniquenessValidator(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public Set<SystemViolation> validate(Product product) {
        Set<SystemViolation> violations = new LinkedHashSet<>();
        if (repository.existsByNameIgnoreCase(product.getName())) {
            violations.add(new SystemViolation("name", "product.name.already.exists"));
        }
        return violations;
    }
}
