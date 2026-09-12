package com.pharm.pharmavigil_platform.validators.batch;

import com.pharm.pharmavigil_platform.domain.Batch;
import com.pharm.pharmavigil_platform.domain.BatchLogEntry;
import com.pharm.pharmavigil_platform.validators.Validator;
import com.pharm.pharmavigil_platform.validators.models.SystemViolation;

import java.util.LinkedHashSet;
import java.util.Set;

public class BatchProductMatchValidator implements Validator<Batch> {

    @Override
    public Set<SystemViolation> validate(Batch batch) {
        Set<SystemViolation> violations = new LinkedHashSet<>();
        if (batch.getId() == null) {
            return violations;
        }
        BatchLogEntry entry = batch.getEntries().get(batch.getEntries().size() - 1);
        if (entry.getProductId() != null && !entry.getProductId().equals(batch.getProductId())) {
            violations.add(new SystemViolation("productId", "batch.product.mismatch"));
        }
        return violations;
    }
}
