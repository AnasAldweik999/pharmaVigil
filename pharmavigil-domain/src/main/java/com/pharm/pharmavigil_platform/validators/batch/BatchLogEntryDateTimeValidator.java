package com.pharm.pharmavigil_platform.validators.batch;

import com.pharm.pharmavigil_platform.domain.Batch;
import com.pharm.pharmavigil_platform.domain.BatchLogEntry;
import com.pharm.pharmavigil_platform.validators.Validator;
import com.pharm.pharmavigil_platform.validators.models.SystemViolation;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;

public class BatchLogEntryDateTimeValidator implements Validator<Batch> {

    @Override
    public Set<SystemViolation> validate(Batch batch) {
        Set<SystemViolation> violations = new LinkedHashSet<>();
        BatchLogEntry entry = batch.getEntries().get(batch.getEntries().size() - 1);
        if (entry.getLineClearanceAt() == null) {
            return violations;
        }

        // Compared against the full date and time, not just the calendar date — a timestamp
        // dated today but later than the current time of day is still in the future.
        if (entry.getLineClearanceAt().isAfter(LocalDateTime.now())) {
            violations.add(new SystemViolation("lineClearanceAt", "batch.line.clearance.in.future"));
        }

        batch.getEntries().stream()
                .filter(other -> other != entry)
                .map(BatchLogEntry::getLineClearanceAt)
                .max(Comparator.naturalOrder())
                .filter(lastLoggedDateTime -> entry.getLineClearanceAt().isBefore(lastLoggedDateTime))
                .ifPresent(lastLoggedDateTime -> violations.add(new SystemViolation("lineClearanceAt", "batch.line.clearance.not.after.last.log")));

        return violations;
    }
}
