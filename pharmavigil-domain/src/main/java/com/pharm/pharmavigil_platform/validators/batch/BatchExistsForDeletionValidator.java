package com.pharm.pharmavigil_platform.validators.batch;

import com.pharm.pharmavigil_platform.domain.BatchLogEntryDeletion;
import com.pharm.pharmavigil_platform.validators.Validator;
import com.pharm.pharmavigil_platform.validators.models.SystemViolation;

import java.util.LinkedHashSet;
import java.util.Set;

public class BatchExistsForDeletionValidator implements Validator<BatchLogEntryDeletion> {

    @Override
    public Set<SystemViolation> validate(BatchLogEntryDeletion deletion) {
        Set<SystemViolation> violations = new LinkedHashSet<>();
        if (deletion.batch() == null) {
            violations.add(new SystemViolation("batchNo", "batch.not.found"));
        }
        return violations;
    }
}
