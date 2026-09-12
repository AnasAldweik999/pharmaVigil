package com.pharm.pharmavigil_platform.config.machine;

import com.pharm.pharmavigil_platform.domain.Machine;
import com.pharm.pharmavigil_platform.repository.MachineRepository;
import com.pharm.pharmavigil_platform.validators.ValidatorChain;
import com.pharm.pharmavigil_platform.validators.machine.MachineExistsValidator;
import com.pharm.pharmavigil_platform.validators.machine.MachineNameNotTakenByOthersValidator;
import com.pharm.pharmavigil_platform.validators.machine.MachineNameValidator;
import com.pharm.pharmavigil_platform.validators.machine.MachineUniquenessValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class MachineValidatorsConfig {

    @Bean
    public MachineNameValidator machineNameValidator() {
        return new MachineNameValidator();
    }

    @Bean
    public MachineUniquenessValidator machineUniquenessValidator(MachineRepository machineRepository) {
        return new MachineUniquenessValidator(machineRepository);
    }

    @Bean
    public MachineNameNotTakenByOthersValidator machineNameNotTakenByOthersValidator(MachineRepository machineRepository) {
        return new MachineNameNotTakenByOthersValidator(machineRepository);
    }

    @Bean
    public MachineExistsValidator machineExistsValidator(MachineRepository machineRepository) {
        return new MachineExistsValidator(machineRepository);
    }

    @Bean
    public ValidatorChain<Machine> machineValidatorChain(
            MachineNameValidator machineNameValidator,
            MachineUniquenessValidator machineUniquenessValidator) {
        return new ValidatorChain<>(List.of(machineNameValidator, machineUniquenessValidator));
    }

    @Bean
    public ValidatorChain<Machine> machineUpdateValidatorChain(
            MachineExistsValidator machineExistsValidator,
            MachineNameValidator machineNameValidator,
            MachineNameNotTakenByOthersValidator machineNameNotTakenByOthersValidator) {
        return new ValidatorChain<>(List.of(machineExistsValidator, machineNameValidator, machineNameNotTakenByOthersValidator));
    }

    @Bean
    public ValidatorChain<Machine> machineToggleActiveValidatorChain(MachineExistsValidator machineExistsValidator) {
        return new ValidatorChain<>(List.of(machineExistsValidator));
    }
}
