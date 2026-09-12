package com.pharm.pharmavigil_platform.validators.workreport;

import com.pharm.pharmavigil_platform.domain.WorkReport;
import com.pharm.pharmavigil_platform.repository.WorkReportRepository;
import com.pharm.pharmavigil_platform.validators.Validator;
import com.pharm.pharmavigil_platform.validators.models.SystemViolation;

import java.util.LinkedHashSet;
import java.util.Set;

public class WorkReportDuplicateValidator implements Validator<WorkReport> {

    private final WorkReportRepository repository;

    public WorkReportDuplicateValidator(WorkReportRepository repository) {
        this.repository = repository;
    }

    @Override
    public Set<SystemViolation> validate(WorkReport report) {
        Set<SystemViolation> violations = new LinkedHashSet<>();
        if (report.getStaffUser() == null || report.getStaffUser().getId() == null
                || report.getShiftId() == null || report.getReportDate() == null) {
            return violations;
        }
        if (repository.existsByStaffUserIdAndShiftIdAndReportDate(
                report.getStaffUser().getId(), report.getShiftId(), report.getReportDate())) {
            violations.add(new SystemViolation("report", "report.duplicate"));
        }
        return violations;
    }
}
