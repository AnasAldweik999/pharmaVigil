package com.pharm.pharmavigil_platform.validators.workreport;

import com.pharm.pharmavigil_platform.domain.WorkReport;
import com.pharm.pharmavigil_platform.repository.ShiftRepository;
import com.pharm.pharmavigil_platform.validators.Validator;
import com.pharm.pharmavigil_platform.validators.models.SystemViolation;

import java.util.LinkedHashSet;
import java.util.Set;

public class WorkReportShiftValidator implements Validator<WorkReport> {

    private final ShiftRepository shiftRepository;

    public WorkReportShiftValidator(ShiftRepository shiftRepository) {
        this.shiftRepository = shiftRepository;
    }

    @Override
    public Set<SystemViolation> validate(WorkReport report) {
        Set<SystemViolation> violations = new LinkedHashSet<>();
        if (report.getShiftId() == null) {
            violations.add(new SystemViolation("shift.id", "shift.id.required"));
            return violations;
        }
        if (!shiftRepository.existsByIdAndActiveTrue(report.getShiftId())) {
            violations.add(new SystemViolation("shift.id", "shift.not.found"));
        }
        return violations;
    }
}
