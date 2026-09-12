package com.pharm.pharmavigil_platform.config.stoptype;

import com.pharm.pharmavigil_platform.domain.StopType;
import com.pharm.pharmavigil_platform.repository.StopTypeRepository;
import com.pharm.pharmavigil_platform.security.IdentityProvider;
import com.pharm.pharmavigil_platform.usecases.stoptype.CreateStopTypeUseCase;
import com.pharm.pharmavigil_platform.usecases.stoptype.ToggleActiveStopTypeUseCase;
import com.pharm.pharmavigil_platform.usecases.stoptype.UpdateStopTypeUseCase;
import com.pharm.pharmavigil_platform.validators.ValidatorChain;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StopTypeUseCaseConfig {

    @Bean
    public CreateStopTypeUseCase createStopTypeUseCase(
            @Qualifier("stopTypeValidatorChain") ValidatorChain<StopType> stopTypeValidatorChain,
            StopTypeRepository stopTypeRepository,
            IdentityProvider identityProvider) {
        return new CreateStopTypeUseCase(stopTypeValidatorChain, stopTypeRepository, identityProvider);
    }

    @Bean
    public UpdateStopTypeUseCase updateStopTypeUseCase(
            @Qualifier("stopTypeUpdateValidatorChain") ValidatorChain<StopType> stopTypeUpdateValidatorChain,
            StopTypeRepository stopTypeRepository,
            IdentityProvider identityProvider) {
        return new UpdateStopTypeUseCase(stopTypeUpdateValidatorChain, stopTypeRepository, identityProvider);
    }

    @Bean
    public ToggleActiveStopTypeUseCase toggleActiveStopTypeUseCase(
            @Qualifier("stopTypeToggleActiveValidatorChain") ValidatorChain<StopType> stopTypeToggleActiveValidatorChain,
            StopTypeRepository stopTypeRepository,
            IdentityProvider identityProvider) {
        return new ToggleActiveStopTypeUseCase(stopTypeToggleActiveValidatorChain, stopTypeRepository, identityProvider);
    }
}
