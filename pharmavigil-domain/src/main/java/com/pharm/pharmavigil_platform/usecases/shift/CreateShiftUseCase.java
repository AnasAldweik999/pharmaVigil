package com.pharm.pharmavigil_platform.usecases.shift;

import com.pharm.pharmavigil_platform.domain.Shift;
import com.pharm.pharmavigil_platform.repository.ShiftRepository;
import com.pharm.pharmavigil_platform.security.IdentityProvider;
import com.pharm.pharmavigil_platform.validators.ValidatorChain;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CreateShiftUseCase {

    private final ValidatorChain<Shift> validators;
    private final ShiftRepository repository;
    private final IdentityProvider identityProvider;

    public CreateShiftUseCase(ValidatorChain<Shift> validators, ShiftRepository repository, IdentityProvider identityProvider) {
        this.validators = validators;
        this.repository = repository;
        this.identityProvider = identityProvider;
    }

    public Shift execute(Shift shift) {
        log.info("Creating shift with name '{}'", shift.getName());
        validators.validate(shift).throwExceptionIfViolated();
        shift.setCreatedBy(identityProvider.getCurrentUser().username());
        Shift created = repository.save(shift);
        log.info("Shift created successfully with id '{}'", created.getId());
        return created;
    }
}
