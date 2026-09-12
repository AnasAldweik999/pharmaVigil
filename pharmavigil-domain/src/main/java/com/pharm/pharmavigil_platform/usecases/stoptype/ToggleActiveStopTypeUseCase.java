package com.pharm.pharmavigil_platform.usecases.stoptype;

import com.pharm.pharmavigil_platform.domain.StopType;
import com.pharm.pharmavigil_platform.repository.StopTypeRepository;
import com.pharm.pharmavigil_platform.security.IdentityProvider;
import com.pharm.pharmavigil_platform.validators.ValidatorChain;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

@Slf4j
public class ToggleActiveStopTypeUseCase {

    private final ValidatorChain<StopType> validators;
    private final StopTypeRepository repository;
    private final IdentityProvider identityProvider;

    public ToggleActiveStopTypeUseCase(ValidatorChain<StopType> validators, StopTypeRepository repository, IdentityProvider identityProvider) {
        this.validators = validators;
        this.repository = repository;
        this.identityProvider = identityProvider;
    }

    public StopType execute(StopType stopType) {
        log.info("Toggling active status for stop type with id '{}'", stopType.getId());
        validators.validate(stopType).throwExceptionIfViolated();
        stopType.setActive(!stopType.isActive());
        stopType.setLastUpdatedAt(Instant.now());
        stopType.setLastUpdatedBy(identityProvider.getCurrentUser().username());
        StopType saved = repository.save(stopType);
        log.info("Stop type '{}' active status changed to '{}'", stopType.getId(), saved.isActive());
        return saved;
    }
}
