package com.pharm.pharmavigil_platform.validators.batch;

import com.pharm.pharmavigil_platform.domain.Batch;
import com.pharm.pharmavigil_platform.validators.Validator;
import com.pharm.pharmavigil_platform.validators.models.SystemViolation;

import java.util.LinkedHashSet;
import java.util.Set;

public class RejectLineClearanceRequiredValidator implements Validator<Batch> {

    @Override
    public Set<SystemViolation> validate(Batch batch) {
        Set<SystemViolation> violations = new LinkedHashSet<>();
        if (batch.getEntries().get(batch.getEntries().size() - 1).getLineClearanceAt() == null) {
            violations.add(new SystemViolation("lineClearanceAt", "batch.reject.line.clearance.required"));
        }
        return violations;
    }
}
