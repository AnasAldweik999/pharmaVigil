package com.pharm.pharmavigil_platform.usecases.stoptype;

import com.pharm.pharmavigil_platform.domain.StopType;
import com.pharm.pharmavigil_platform.repository.StopTypeRepository;
import com.pharm.pharmavigil_platform.security.IdentityProvider;
import com.pharm.pharmavigil_platform.validators.ValidatorChain;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

@Slf4j
public class UpdateStopTypeUseCase {

    private final ValidatorChain<StopType> validators;
    private final StopTypeRepository repository;
    private final IdentityProvider identityProvider;

    public UpdateStopTypeUseCase(ValidatorChain<StopType> validators, StopTypeRepository repository, IdentityProvider identityProvider) {
        this.validators = validators;
        this.repository = repository;
        this.identityProvider = identityProvider;
    }

    public StopType execute(StopType stopType) {
        log.info("Updating stop type with id '{}'", stopType.getId());
        validators.validate(stopType).throwExceptionIfViolated();
        stopType.setLastUpdatedAt(Instant.now());
        stopType.setLastUpdatedBy(identityProvider.getCurrentUser().username());
        StopType updated = repository.save(stopType);
        log.info("Stop type updated successfully with id '{}'", updated.getId());
        return updated;
    }
}
