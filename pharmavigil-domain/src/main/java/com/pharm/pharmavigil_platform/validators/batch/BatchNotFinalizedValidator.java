package com.pharm.pharmavigil_platform.validators.batch;

import com.pharm.pharmavigil_platform.domain.Batch;
import com.pharm.pharmavigil_platform.domain.BatchStatus;
import com.pharm.pharmavigil_platform.validators.Validator;
import com.pharm.pharmavigil_platform.validators.models.SystemViolation;

import java.util.LinkedHashSet;
import java.util.Set;

public class BatchNotFinalizedValidator implements Validator<Batch> {

    @Override
    public Set<SystemViolation> validate(Batch batch) {
        Set<SystemViolation> violations = new LinkedHashSet<>();
        if (batch.getId() == null) {
            return violations;
        }
        if (batch.getStatus() == BatchStatus.REJECTED) {
            violations.add(new SystemViolation("batchNo", "batch.rejected"));
        } else if (batch.getStatus() == BatchStatus.COMPLETED) {
            violations.add(new SystemViolation("batchNo", "batch.completed"));
        }
        return violations;
    }
}
