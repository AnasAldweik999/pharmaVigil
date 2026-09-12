package com.pharm.pharmavigil_platform.validators.batch;

import com.pharm.pharmavigil_platform.domain.Batch;
import com.pharm.pharmavigil_platform.domain.BatchLogEntry;
import com.pharm.pharmavigil_platform.repository.ProductRepository;
import com.pharm.pharmavigil_platform.validators.Validator;
import com.pharm.pharmavigil_platform.validators.models.SystemViolation;

import java.util.LinkedHashSet;
import java.util.Set;

public class BatchProductExistsValidator implements Validator<Batch> {

    private final ProductRepository productRepository;

    public BatchProductExistsValidator(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Set<SystemViolation> validate(Batch batch) {
        Set<SystemViolation> violations = new LinkedHashSet<>();
        BatchLogEntry entry = batch.getEntries().get(batch.getEntries().size() - 1);
        if (entry.getProductId() != null && !productRepository.existsByIdAndActiveTrue(entry.getProductId())) {
            violations.add(new SystemViolation("productId", "batch.product.not.found"));
        }
        return violations;
    }
}
