package com.pharm.pharmavigil_platform.repository.adapter;

import com.pharm.pharmavigil_platform.domain.AccountType;
import com.pharm.pharmavigil_platform.domain.User;
import com.pharm.pharmavigil_platform.domain.UserStatus;
import com.pharm.pharmavigil_platform.mapper.UserMapper;
import com.pharm.pharmavigil_platform.repository.UserRepository;
import com.pharm.pharmavigil_platform.repository.entities.UserEntity;
import com.pharm.pharmavigil_platform.repository.jpa.JpaUserRepository;
import com.pharm.pharmavigil_platform.repository.listing.UserListing;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;



@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository, UserListing {

    private final JpaUserRepository jpaUserRepository;
    private final UserMapper userMapper;

    @Override
    public boolean existsById(UUID id) {
        return jpaUserRepository.existsById(id);
    }

    @Override
    public boolean existsByEmailIgnoreCase(String email) {
        return jpaUserRepository.existsByEmailIgnoreCase(email);
    }

    @Override
    public boolean existsByEmailIgnoreCaseAndIdNot(String email, UUID id) {
        return jpaUserRepository.existsByEmailIgnoreCaseAndIdNot(email, id);
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaUserRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByUsernameAndIdNot(String username, UUID id) {
        return jpaUserRepository.existsByUsernameAndIdNot(username, id);
    }

    @Override
    public Optional<User> findByEmailIgnoreCase(String email) {
        return jpaUserRepository.findByEmailIgnoreCase(email).map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jpaUserRepository.findByUsername(username).map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaUserRepository.findById(id).map(userMapper::toDomain);
    }

    @Override
    public List<User> findActiveByAccountType(AccountType accountType) {
        return jpaUserRepository.findByStatusAndAccountType(UserStatus.ACTIVE, accountType).stream()
                .map(userMapper::toDomain)
                .toList();
    }

    @Override
    public List<User> findAllActive() {
        return jpaUserRepository.findByStatus(UserStatus.ACTIVE).stream()
                .map(userMapper::toDomain)
                .toList();
    }

    @Override
    public List<User> findAllByIdIn(Collection<UUID> ids) {
        return jpaUserRepository.findByIdIn(ids).stream()
                .map(userMapper::toDomain)
                .toList();
    }

    @Override
    public User save(User user) {
        return userMapper.toDomain(jpaUserRepository.save(userMapper.toEntity(user)));
    }

    @Override
    public Page<UserEntity> findAll(Specification<UserEntity> spec, Pageable pageable) {
        return jpaUserRepository.findAll(spec, pageable);
    }
}
