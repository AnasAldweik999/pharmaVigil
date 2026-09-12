package com.pharm.pharmavigil_platform.validators.batch;

import com.pharm.pharmavigil_platform.domain.BatchLogEntry;
import com.pharm.pharmavigil_platform.domain.BatchLogEntryDeletion;
import com.pharm.pharmavigil_platform.validators.Validator;
import com.pharm.pharmavigil_platform.validators.models.SystemViolation;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;

public class BatchLogEntryIsLastValidator implements Validator<BatchLogEntryDeletion> {

    @Override
    public Set<SystemViolation> validate(BatchLogEntryDeletion deletion) {
        Set<SystemViolation> violations = new LinkedHashSet<>();
        if (deletion.batch() == null
                || deletion.batch().getEntries().stream().noneMatch(entry -> deletion.entryId().equals(entry.getId()))) {
            return violations;
        }

        BatchLogEntry latest = deletion.batch().getEntries().stream()
                .max(Comparator.comparing(BatchLogEntry::getLineClearanceAt)
                        .thenComparing(BatchLogEntry::getCreatedAt, Comparator.nullsFirst(Comparator.naturalOrder())))
                .orElseThrow();
        if (!deletion.entryId().equals(latest.getId())) {
            violations.add(new SystemViolation("entryId", "batch.log.entry.not.last"));
        }
        return violations;
    }
}
