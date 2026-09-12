package com.pharm.pharmavigil_platform.usecases.stoptype;

import com.pharm.pharmavigil_platform.domain.StopType;
import com.pharm.pharmavigil_platform.repository.StopTypeRepository;
import com.pharm.pharmavigil_platform.security.IdentityProvider;
import com.pharm.pharmavigil_platform.validators.ValidatorChain;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CreateStopTypeUseCase {

    private final ValidatorChain<StopType> validators;
    private final StopTypeRepository repository;
    private final IdentityProvider identityProvider;

    public CreateStopTypeUseCase(ValidatorChain<StopType> validators, StopTypeRepository repository, IdentityProvider identityProvider) {
        this.validators = validators;
        this.repository = repository;
        this.identityProvider = identityProvider;
    }

    public StopType execute(StopType stopType) {
        log.info("Creating stop type with name '{}'", stopType.getName());
        validators.validate(stopType).throwExceptionIfViolated();
        stopType.setCreatedBy(identityProvider.getCurrentUser().username());
        StopType created = repository.save(stopType);
        log.info("Stop type created successfully with id '{}'", created.getId());
        return created;
    }
}
