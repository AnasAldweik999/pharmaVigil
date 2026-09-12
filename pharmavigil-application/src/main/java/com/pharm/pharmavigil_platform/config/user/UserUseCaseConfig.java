package com.pharm.pharmavigil_platform.config.user;

import com.pharm.pharmavigil_platform.domain.User;
import com.pharm.pharmavigil_platform.repository.UserRepository;
import com.pharm.pharmavigil_platform.security.IdentityProvider;
import com.pharm.pharmavigil_platform.usecases.user.*;
import com.pharm.pharmavigil_platform.validators.ValidatorChain;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserUseCaseConfig {

    @Bean
    public CreateUserUseCase createUserUseCase(
            @Qualifier("userCreateValidatorChain") ValidatorChain<User> chain,
            UserRepository userRepository,
            IdentityProvider identityProvider) {
        return new CreateUserUseCase(chain, userRepository, identityProvider);
    }

    @Bean
    public UpdateUserUseCase updateUserUseCase(
            @Qualifier("userUpdateValidatorChain") ValidatorChain<User> chain,
            UserRepository userRepository,
            IdentityProvider identityProvider) {
        return new UpdateUserUseCase(chain, userRepository, identityProvider);
    }

    @Bean
    public ToggleActiveUseCase toggleActiveUseCase(
            @Qualifier("userToggleValidatorChain") ValidatorChain<User> chain,
            UserRepository userRepository,
            IdentityProvider identityProvider) {
        return new ToggleActiveUseCase(chain, userRepository, identityProvider);
    }
}
