package com.pharm.pharmavigil_platform.usecases.workreport;

import com.pharm.pharmavigil_platform.domain.User;
import com.pharm.pharmavigil_platform.domain.WorkReport;
import com.pharm.pharmavigil_platform.repository.WorkReportRepository;
import com.pharm.pharmavigil_platform.security.IdentityProvider;
import com.pharm.pharmavigil_platform.validators.ValidatorChain;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

@Slf4j
public class CreateWorkReportUseCase {

    private final ValidatorChain<WorkReport> validators;
    private final EnrichWorkReportUseCase enrichWorkReportUseCase;
    private final WorkReportRepository repository;
    private final IdentityProvider identityProvider;

    public CreateWorkReportUseCase(ValidatorChain<WorkReport> validators,
                                    EnrichWorkReportUseCase enrichWorkReportUseCase,
                                    WorkReportRepository repository,
                                    IdentityProvider identityProvider) {
        this.validators = validators;
        this.enrichWorkReportUseCase = enrichWorkReportUseCase;
        this.repository = repository;
        this.identityProvider = identityProvider;
    }

    public WorkReport execute(WorkReport workReport) {
        prepare(workReport);
        log.info("Creating work report for staff '{}' on date '{}'",
                workReport.getStaffUser().getId(), workReport.getReportDate());
        validators.validate(workReport).throwExceptionIfViolated();
        enrichWorkReportUseCase.execute(workReport);
        WorkReport saved = repository.save(workReport);
        log.info("Work report created successfully with id '{}'", saved.getId());
        return saved;
    }

    private void prepare(WorkReport workReport) {
        Instant now = Instant.now();
        workReport.setStaffUser(User.builder().id(identityProvider.getCurrentUser().id()).build());
        workReport.setCreatedAt(now);
        workReport.setUpdatedAt(now);
    }
}
