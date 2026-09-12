package com.pharm.pharmavigil_platform.usecases.machine;

import com.pharm.pharmavigil_platform.domain.Machine;
import com.pharm.pharmavigil_platform.repository.MachineRepository;
import com.pharm.pharmavigil_platform.security.IdentityProvider;
import com.pharm.pharmavigil_platform.validators.ValidatorChain;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

@Slf4j
public class UpdateMachineUseCase {

    private final ValidatorChain<Machine> validators;
    private final MachineRepository repository;
    private final IdentityProvider identityProvider;

    public UpdateMachineUseCase(ValidatorChain<Machine> validators, MachineRepository repository, IdentityProvider identityProvider) {
        this.validators = validators;
        this.repository = repository;
        this.identityProvider = identityProvider;
    }

    public Machine execute(Machine machine) {
        log.info("Updating machine with id '{}'", machine.getId());
        validators.validate(machine).throwExceptionIfViolated();
        machine.setLastUpdatedAt(Instant.now());
        machine.setLastUpdatedBy(identityProvider.getCurrentUser().username());
        Machine updated = repository.save(machine);
        log.info("Machine updated successfully with id '{}'", updated.getId());
        return updated;
    }
}
