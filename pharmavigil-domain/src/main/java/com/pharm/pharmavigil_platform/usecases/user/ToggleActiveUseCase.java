package com.pharm.pharmavigil_platform.usecases.user;

import com.pharm.pharmavigil_platform.domain.User;
import com.pharm.pharmavigil_platform.domain.UserStatus;
import com.pharm.pharmavigil_platform.repository.UserRepository;
import com.pharm.pharmavigil_platform.security.IdentityProvider;
import com.pharm.pharmavigil_platform.validators.ValidatorChain;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

@Slf4j
public class ToggleActiveUseCase {

    private final ValidatorChain<User> validators;
    private final UserRepository repository;
    private final IdentityProvider identityProvider;

    public ToggleActiveUseCase(ValidatorChain<User> validators, UserRepository repository, IdentityProvider identityProvider) {
        this.validators = validators;
        this.repository = repository;
        this.identityProvider = identityProvider;
    }

    public User execute(User user) {
        log.info("Toggling active status for user with id '{}'", user.getId());
        validators.validate(user).throwExceptionIfViolated();
        UserStatus newStatus = user.getStatus() == UserStatus.ACTIVE
                ? UserStatus.INACTIVE
                : UserStatus.ACTIVE;
        user.setStatus(newStatus);
        user.setLastUpdatedAt(Instant.now());
        user.setLastUpdatedBy(identityProvider.getCurrentUser().username());
        User saved = repository.save(user);
        log.info("User '{}' status changed to '{}'", user.getId(), newStatus);
        return saved;
    }
}
