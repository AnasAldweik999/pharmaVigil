package com.pharm.pharmavigil_platform.config.machine;

import com.pharm.pharmavigil_platform.domain.Machine;
import com.pharm.pharmavigil_platform.repository.MachineRepository;
import com.pharm.pharmavigil_platform.security.IdentityProvider;
import com.pharm.pharmavigil_platform.usecases.machine.CreateMachineUseCase;
import com.pharm.pharmavigil_platform.usecases.machine.ToggleActiveMachineUseCase;
import com.pharm.pharmavigil_platform.usecases.machine.UpdateMachineUseCase;
import com.pharm.pharmavigil_platform.validators.ValidatorChain;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MachineUseCaseConfig {

    @Bean
    public CreateMachineUseCase createMachineUseCase(
            @Qualifier("machineValidatorChain") ValidatorChain<Machine> machineValidatorChain,
            MachineRepository machineRepository,
            IdentityProvider identityProvider) {
        return new CreateMachineUseCase(machineValidatorChain, machineRepository, identityProvider);
    }

    @Bean
    public UpdateMachineUseCase updateMachineUseCase(
            @Qualifier("machineUpdateValidatorChain") ValidatorChain<Machine> machineUpdateValidatorChain,
            MachineRepository machineRepository,
            IdentityProvider identityProvider) {
        return new UpdateMachineUseCase(machineUpdateValidatorChain, machineRepository, identityProvider);
    }

    @Bean
    public ToggleActiveMachineUseCase toggleActiveMachineUseCase(
            @Qualifier("machineToggleActiveValidatorChain") ValidatorChain<Machine> machineToggleActiveValidatorChain,
            MachineRepository machineRepository,
            IdentityProvider identityProvider) {
        return new ToggleActiveMachineUseCase(machineToggleActiveValidatorChain, machineRepository, identityProvider);
    }
}
