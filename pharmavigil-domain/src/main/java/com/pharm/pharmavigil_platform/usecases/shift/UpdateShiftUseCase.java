package com.pharm.pharmavigil_platform.usecases.shift;

import com.pharm.pharmavigil_platform.domain.Shift;
import com.pharm.pharmavigil_platform.repository.ShiftRepository;
import com.pharm.pharmavigil_platform.security.IdentityProvider;
import com.pharm.pharmavigil_platform.validators.ValidatorChain;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

@Slf4j
public class UpdateShiftUseCase {

    private final ValidatorChain<Shift> validators;
    private final ShiftRepository repository;
    private final IdentityProvider identityProvider;

    public UpdateShiftUseCase(ValidatorChain<Shift> validators, ShiftRepository repository, IdentityProvider identityProvider) {
        this.validators = validators;
        this.repository = repository;
        this.identityProvider = identityProvider;
    }

    public Shift execute(Shift shift) {
        log.info("Updating shift with id '{}'", shift.getId());
        validators.validate(shift).throwExceptionIfViolated();
        shift.setLastUpdatedAt(Instant.now());
        shift.setLastUpdatedBy(identityProvider.getCurrentUser().username());
        Shift updated = repository.save(shift);
        log.info("Shift updated successfully with id '{}'", updated.getId());
        return updated;
    }
}
