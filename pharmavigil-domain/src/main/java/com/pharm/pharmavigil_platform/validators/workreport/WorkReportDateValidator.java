package com.pharm.pharmavigil_platform.validators.workreport;

import com.pharm.pharmavigil_platform.domain.WorkReport;
import com.pharm.pharmavigil_platform.validators.Validator;
import com.pharm.pharmavigil_platform.validators.models.SystemViolation;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

public class WorkReportDateValidator implements Validator<WorkReport> {

    @Override
    public Set<SystemViolation> validate(WorkReport report) {
        Set<SystemViolation> violations = new LinkedHashSet<>();
        if (report.getReportDate() != null && report.getReportDate().isAfter(LocalDate.now())) {
            violations.add(new SystemViolation("report.date", "report.date.future.not.allowed"));
        }
        return violations;
    }
}
