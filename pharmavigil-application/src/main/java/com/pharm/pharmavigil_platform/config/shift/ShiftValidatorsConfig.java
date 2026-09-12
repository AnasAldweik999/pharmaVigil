package com.pharm.pharmavigil_platform.config.shift;

import com.pharm.pharmavigil_platform.domain.Shift;
import com.pharm.pharmavigil_platform.repository.ShiftRepository;
import com.pharm.pharmavigil_platform.validators.ValidatorChain;
import com.pharm.pharmavigil_platform.validators.shift.ShiftExistsValidator;
import com.pharm.pharmavigil_platform.validators.shift.ShiftNameNotTakenByOthersValidator;
import com.pharm.pharmavigil_platform.validators.shift.ShiftNameValidator;
import com.pharm.pharmavigil_platform.validators.shift.ShiftUniquenessValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ShiftValidatorsConfig {

    @Bean
    public ShiftNameValidator shiftNameValidator() {
        return new ShiftNameValidator();
    }

    @Bean
    public ShiftUniquenessValidator shiftUniquenessValidator(ShiftRepository shiftRepository) {
        return new ShiftUniquenessValidator(shiftRepository);
    }

    @Bean
    public ShiftNameNotTakenByOthersValidator shiftNameNotTakenByOthersValidator(ShiftRepository shiftRepository) {
        return new ShiftNameNotTakenByOthersValidator(shiftRepository);
    }

    @Bean
    public ShiftExistsValidator shiftExistsValidator(ShiftRepository shiftRepository) {
        return new ShiftExistsValidator(shiftRepository);
    }

    @Bean
    public ValidatorChain<Shift> shiftValidatorChain(
            ShiftNameValidator shiftNameValidator,
            ShiftUniquenessValidator shiftUniquenessValidator) {
        return new ValidatorChain<>(List.of(shiftNameValidator, shiftUniquenessValidator));
    }

    @Bean
    public ValidatorChain<Shift> shiftUpdateValidatorChain(
            ShiftExistsValidator shiftExistsValidator,
            ShiftNameValidator shiftNameValidator,
            ShiftNameNotTakenByOthersValidator shiftNameNotTakenByOthersValidator) {
        return new ValidatorChain<>(List.of(shiftExistsValidator, shiftNameValidator, shiftNameNotTakenByOthersValidator));
    }

    @Bean
    public ValidatorChain<Shift> shiftToggleActiveValidatorChain(ShiftExistsValidator shiftExistsValidator) {
        return new ValidatorChain<>(List.of(shiftExistsValidator));
    }
}
