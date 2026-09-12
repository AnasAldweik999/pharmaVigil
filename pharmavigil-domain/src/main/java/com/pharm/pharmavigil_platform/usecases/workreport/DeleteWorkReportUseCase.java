package com.pharm.pharmavigil_platform.usecases.workreport;

import com.pharm.pharmavigil_platform.domain.WorkReport;
import com.pharm.pharmavigil_platform.repository.WorkReportRepository;
import com.pharm.pharmavigil_platform.validators.ValidatorChain;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeleteWorkReportUseCase {

    private final ValidatorChain<WorkReport> validators;
    private final WorkReportRepository repository;

    public DeleteWorkReportUseCase(ValidatorChain<WorkReport> validators, WorkReportRepository repository) {
        this.validators = validators;
        this.repository = repository;
    }

    public void execute(WorkReport workReport) {
        log.info("Deleting work report with id '{}'", workReport.getId());
        validators.validate(workReport).throwExceptionIfViolated();
        repository.deleteById(workReport.getId());
        log.info("Work report deleted successfully with id '{}'", workReport.getId());
    }
}
