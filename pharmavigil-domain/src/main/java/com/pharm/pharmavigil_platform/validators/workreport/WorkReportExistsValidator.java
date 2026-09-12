package com.pharm.pharmavigil_platform.validators.workreport;

import com.pharm.pharmavigil_platform.domain.WorkReport;
import com.pharm.pharmavigil_platform.repository.WorkReportRepository;
import com.pharm.pharmavigil_platform.validators.Validator;
import com.pharm.pharmavigil_platform.validators.models.SystemViolation;

import java.util.LinkedHashSet;
import java.util.Set;

public class WorkReportExistsValidator implements Validator<WorkReport> {

    private final WorkReportRepository repository;

    public WorkReportExistsValidator(WorkReportRepository repository) {
        this.repository = repository;
    }

    @Override
    public Set<SystemViolation> validate(WorkReport workReport) {
        Set<SystemViolation> violations = new LinkedHashSet<>();
        if (!repository.existsById(workReport.getId())) {
            violations.add(new SystemViolation("id", "work.report.not.found"));
        }
        return violations;
    }
}
