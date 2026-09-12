package com.pharm.pharmavigil_platform.validators.batch;

import com.pharm.pharmavigil_platform.domain.Batch;
import com.pharm.pharmavigil_platform.validators.Validator;
import com.pharm.pharmavigil_platform.validators.models.SystemViolation;

import java.util.LinkedHashSet;
import java.util.Set;

public class RejectReasonValidator implements Validator<Batch> {

    @Override
    public Set<SystemViolation> validate(Batch batch) {
        Set<SystemViolation> violations = new LinkedHashSet<>();
        String reason = batch.getEntries().get(batch.getEntries().size() - 1).getRejectedReason();
        if (reason == null || reason.isBlank()) {
            violations.add(new SystemViolation("reason", "batch.reject.reason.required"));
        }
        return violations;
    }
}
