package com.pharm.pharmavigil_platform.config.shift;

import com.pharm.pharmavigil_platform.domain.Shift;
import com.pharm.pharmavigil_platform.repository.ShiftRepository;
import com.pharm.pharmavigil_platform.security.IdentityProvider;
import com.pharm.pharmavigil_platform.usecases.shift.CreateShiftUseCase;
import com.pharm.pharmavigil_platform.usecases.shift.ToggleActiveShiftUseCase;
import com.pharm.pharmavigil_platform.usecases.shift.UpdateShiftUseCase;
import com.pharm.pharmavigil_platform.validators.ValidatorChain;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShiftUseCaseConfig {

    @Bean
    public CreateShiftUseCase createShiftUseCase(
            @Qualifier("shiftValidatorChain") ValidatorChain<Shift> shiftValidatorChain,
            ShiftRepository shiftRepository,
            IdentityProvider identityProvider) {
        return new CreateShiftUseCase(shiftValidatorChain, shiftRepository, identityProvider);
    }

    @Bean
    public UpdateShiftUseCase updateShiftUseCase(
            @Qualifier("shiftUpdateValidatorChain") ValidatorChain<Shift> shiftUpdateValidatorChain,
            ShiftRepository shiftRepository,
            IdentityProvider identityProvider) {
        return new UpdateShiftUseCase(shiftUpdateValidatorChain, shiftRepository, identityProvider);
    }

    @Bean
    public ToggleActiveShiftUseCase toggleActiveShiftUseCase(
            @Qualifier("shiftToggleActiveValidatorChain") ValidatorChain<Shift> shiftToggleActiveValidatorChain,
            ShiftRepository shiftRepository,
            IdentityProvider identityProvider) {
        return new ToggleActiveShiftUseCase(shiftToggleActiveValidatorChain, shiftRepository, identityProvider);
    }
}
