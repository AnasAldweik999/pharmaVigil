package com.pharm.pharmavigil_platform.usecases.user;

import com.pharm.pharmavigil_platform.domain.User;
import com.pharm.pharmavigil_platform.repository.UserRepository;
import com.pharm.pharmavigil_platform.security.IdentityProvider;
import com.pharm.pharmavigil_platform.validators.ValidatorChain;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

@Slf4j
public class CreateUserUseCase {

    private final ValidatorChain<User> validators;
    private final UserRepository repository;
    private final IdentityProvider identityProvider;

    public CreateUserUseCase(ValidatorChain<User> validators, UserRepository repository, IdentityProvider identityProvider) {
        this.validators = validators;
        this.repository = repository;
        this.identityProvider = identityProvider;
    }

    public User execute(User user) {
        log.info("Creating user with email '{}'", user.getEmail());
        validators.validate(user).throwExceptionIfViolated();
        String currentUsername = identityProvider.getCurrentUser().username();
        user.setCreatedBy(currentUsername);
        user.setLastUpdatedAt(Instant.now());
        user.setLastUpdatedBy(currentUsername);
        User created = repository.save(user);
        log.info("User created successfully with id '{}'", created.getId());
        return created;
    }
}
