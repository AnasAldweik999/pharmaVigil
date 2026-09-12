package com.pharm.pharmavigil_platform.config.stoptype;

import com.pharm.pharmavigil_platform.domain.StopType;
import com.pharm.pharmavigil_platform.repository.StopTypeRepository;
import com.pharm.pharmavigil_platform.validators.ValidatorChain;
import com.pharm.pharmavigil_platform.validators.stoptype.StopTypeExistsValidator;
import com.pharm.pharmavigil_platform.validators.stoptype.StopTypeNameNotTakenByOthersValidator;
import com.pharm.pharmavigil_platform.validators.stoptype.StopTypeNameValidator;
import com.pharm.pharmavigil_platform.validators.stoptype.StopTypeUniquenessValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class StopTypeValidatorsConfig {

    @Bean
    public StopTypeNameValidator stopTypeNameValidator() {
        return new StopTypeNameValidator();
    }

    @Bean
    public StopTypeUniquenessValidator stopTypeUniquenessValidator(StopTypeRepository stopTypeRepository) {
        return new StopTypeUniquenessValidator(stopTypeRepository);
    }

    @Bean
    public StopTypeNameNotTakenByOthersValidator stopTypeNameNotTakenByOthersValidator(StopTypeRepository stopTypeRepository) {
        return new StopTypeNameNotTakenByOthersValidator(stopTypeRepository);
    }

    @Bean
    public StopTypeExistsValidator stopTypeExistsValidator(StopTypeRepository stopTypeRepository) {
        return new StopTypeExistsValidator(stopTypeRepository);
    }

    @Bean
    public ValidatorChain<StopType> stopTypeValidatorChain(
            StopTypeNameValidator stopTypeNameValidator,
            StopTypeUniquenessValidator stopTypeUniquenessValidator) {
        return new ValidatorChain<>(List.of(stopTypeNameValidator, stopTypeUniquenessValidator));
    }

    @Bean
    public ValidatorChain<StopType> stopTypeUpdateValidatorChain(
            StopTypeExistsValidator stopTypeExistsValidator,
            StopTypeNameValidator stopTypeNameValidator,
            StopTypeNameNotTakenByOthersValidator stopTypeNameNotTakenByOthersValidator) {
        return new ValidatorChain<>(List.of(stopTypeExistsValidator, stopTypeNameValidator, stopTypeNameNotTakenByOthersValidator));
    }

    @Bean
    public ValidatorChain<StopType> stopTypeToggleActiveValidatorChain(StopTypeExistsValidator stopTypeExistsValidator) {
        return new ValidatorChain<>(List.of(stopTypeExistsValidator));
    }
}
