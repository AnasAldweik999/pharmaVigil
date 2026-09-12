package com.pharm.pharmavigil_platform.usecases.machine;

import com.pharm.pharmavigil_platform.domain.Machine;
import com.pharm.pharmavigil_platform.repository.MachineRepository;
import com.pharm.pharmavigil_platform.security.IdentityProvider;
import com.pharm.pharmavigil_platform.validators.ValidatorChain;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CreateMachineUseCase {

    private final ValidatorChain<Machine> validators;
    private final MachineRepository repository;
    private final IdentityProvider identityProvider;

    public CreateMachineUseCase(ValidatorChain<Machine> validators, MachineRepository repository, IdentityProvider identityProvider) {
        this.validators = validators;
        this.repository = repository;
        this.identityProvider = identityProvider;
    }

    public Machine execute(Machine machine) {
        log.info("Creating machine with name '{}'", machine.getName());
        validators.validate(machine).throwExceptionIfViolated();
        machine.setCreatedBy(identityProvider.getCurrentUser().username());
        Machine created = repository.save(machine);
        log.info("Machine created successfully with id '{}'", created.getId());
        return created;
    }
}
