package com.pharm.pharmavigil_platform.validators.batch;

import com.pharm.pharmavigil_platform.domain.BatchLogEntryDeletion;
import com.pharm.pharmavigil_platform.validators.Validator;
import com.pharm.pharmavigil_platform.validators.models.SystemViolation;

import java.util.LinkedHashSet;
import java.util.Set;

public class BatchLogEntryExistsValidator implements Validator<BatchLogEntryDeletion> {

    @Override
    public Set<SystemViolation> validate(BatchLogEntryDeletion deletion) {
        Set<SystemViolation> violations = new LinkedHashSet<>();
        if (deletion.batch() != null
                && deletion.batch().getEntries().stream().noneMatch(entry -> deletion.entryId().equals(entry.getId()))) {
            violations.add(new SystemViolation("entryId", "batch.log.entry.not.found"));
        }
        return violations;
    }
}
