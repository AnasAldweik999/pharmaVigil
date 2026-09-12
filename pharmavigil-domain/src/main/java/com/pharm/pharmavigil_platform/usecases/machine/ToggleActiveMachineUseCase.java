package com.pharm.pharmavigil_platform.usecases.machine;

import com.pharm.pharmavigil_platform.domain.Machine;
import com.pharm.pharmavigil_platform.repository.MachineRepository;
import com.pharm.pharmavigil_platform.security.IdentityProvider;
import com.pharm.pharmavigil_platform.validators.ValidatorChain;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

@Slf4j
public class ToggleActiveMachineUseCase {

    private final ValidatorChain<Machine> validators;
    private final MachineRepository repository;
    private final IdentityProvider identityProvider;

    public ToggleActiveMachineUseCase(ValidatorChain<Machine> validators, MachineRepository repository, IdentityProvider identityProvider) {
        this.validators = validators;
        this.repository = repository;
        this.identityProvider = identityProvider;
    }

    public Machine execute(Machine machine) {
        log.info("Toggling active status for machine with id '{}'", machine.getId());
        validators.validate(machine).throwExceptionIfViolated();
        machine.setActive(!machine.isActive());
        machine.setLastUpdatedAt(Instant.now());
        machine.setLastUpdatedBy(identityProvider.getCurrentUser().username());
        Machine saved = repository.save(machine);
        log.info("Machine '{}' active status changed to '{}'", machine.getId(), saved.isActive());
        return saved;
    }
}
