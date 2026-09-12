package com.pharm.pharmavigil_platform.usecases.user;

import com.pharm.pharmavigil_platform.domain.User;
import com.pharm.pharmavigil_platform.repository.UserRepository;
import com.pharm.pharmavigil_platform.security.IdentityProvider;
import com.pharm.pharmavigil_platform.validators.ValidatorChain;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

@Slf4j
public class UpdateUserUseCase {

    private final ValidatorChain<User> validators;
    private final UserRepository repository;
    private final IdentityProvider identityProvider;

    public UpdateUserUseCase(ValidatorChain<User> validators, UserRepository repository, IdentityProvider identityProvider) {
        this.validators = validators;
        this.repository = repository;
        this.identityProvider = identityProvider;
    }

    public User execute(User user) {
        log.info("Updating user with id '{}'", user.getId());
        validators.validate(user).throwExceptionIfViolated();
        user.setLastUpdatedAt(Instant.now());
        user.setLastUpdatedBy(identityProvider.getCurrentUser().username());
        User updated = repository.save(user);
        log.info("User updated successfully with id '{}'", updated.getId());
        return updated;
    }
}
