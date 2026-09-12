package com.pharm.pharmavigil_platform.validators.batch;

import com.pharm.pharmavigil_platform.domain.Batch;
import com.pharm.pharmavigil_platform.domain.BatchLogEntry;
import com.pharm.pharmavigil_platform.validators.Validator;
import com.pharm.pharmavigil_platform.validators.models.SystemViolation;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class BatchLogEntryDetailsValidator implements Validator<Batch> {

    private final Pattern batchNoPattern;

    public BatchLogEntryDetailsValidator(String batchNoRegex) {
        this.batchNoPattern = Pattern.compile(batchNoRegex);
    }

    @Override
    public Set<SystemViolation> validate(Batch batch) {
        Set<SystemViolation> violations = new LinkedHashSet<>();

        if (batch.getBatchNo() == null || batch.getBatchNo().isBlank()) {
            violations.add(new SystemViolation("batchNo", "batch.no.required"));
        } else if (!batchNoPattern.matcher(batch.getBatchNo()).matches()) {
            violations.add(new SystemViolation("batchNo", "batch.no.invalid.format"));
        }

        BatchLogEntry entry = batch.getEntries().get(batch.getEntries().size() - 1);
        if (entry.getProductId() == null) {
            violations.add(new SystemViolation("productId", "batch.product.id.required"));
        }
        if (!entry.isCompleted() && entry.getDepartmentId() == null) {
            violations.add(new SystemViolation("departmentId", "batch.department.id.required"));
        }
        if (!entry.isCompleted() && entry.getMachineId() == null) {
            violations.add(new SystemViolation("machineId", "batch.machine.id.required"));
        }
        if (entry.getLineClearanceAt() == null) {
            violations.add(new SystemViolation("lineClearanceAt", "batch.line.clearance.required"));
        }
        return violations;
    }
}
