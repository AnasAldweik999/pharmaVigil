package com.pharm.pharmavigil_platform.repository;

import com.pharm.pharmavigil_platform.domain.AccountType;
import com.pharm.pharmavigil_platform.domain.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    boolean existsById(UUID id);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCaseAndIdNot(String email, UUID id);
    boolean existsByUsername(String username);
    boolean existsByUsernameAndIdNot(String username, UUID id);
    Optional<User> findByEmailIgnoreCase(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findById(UUID id);
    List<User> findActiveByAccountType(AccountType accountType);
    List<User> findAllActive();
    List<User> findAllByIdIn(Collection<UUID> ids);
    User save(User user);
}
