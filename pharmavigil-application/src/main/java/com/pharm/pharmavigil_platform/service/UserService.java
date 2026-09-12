package com.pharm.pharmavigil_platform.service;

import com.pharm.pharmavigil_platform.domain.AccountType;
import com.pharm.pharmavigil_platform.domain.User;
import com.pharm.pharmavigil_platform.domain.UserRole;
import com.pharm.pharmavigil_platform.domain.UserStatus;
import com.pharm.pharmavigil_platform.exception.ResourceNotFoundException;
import com.pharm.pharmavigil_platform.mapper.UserMapper;
import com.pharm.pharmavigil_platform.repository.UserRepository;
import com.pharm.pharmavigil_platform.repository.entities.UserEntity;
import com.pharm.pharmavigil_platform.repository.listing.UserListing;
import com.pharm.pharmavigil_platform.repository.specs.StaffUserSearchSpec;
import com.pharm.pharmavigil_platform.repository.specs.SupervisorUserSearchSpec;
import com.pharm.pharmavigil_platform.repository.specs.UserSpec;
import com.pharm.pharmavigil_platform.resources.supervisor.StaffUserDropdownResponse;
import com.pharm.pharmavigil_platform.resources.supervisor.SupervisorUserDropdownResponse;
import com.pharm.pharmavigil_platform.resources.user.CreateUserRequest;
import com.pharm.pharmavigil_platform.resources.user.UpdateUserRequest;
import com.pharm.pharmavigil_platform.resources.user.UserResponse;
import com.pharm.pharmavigil_platform.usecases.user.*;
import com.pharm.pharmavigil_platform.validators.exceptions.SystemViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {


    private final UserListing userListing;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CreateUserUseCase createUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final ToggleActiveUseCase toggleActiveUseCase;
    private final AuthService authService;
    private final EmailService emailService;

    @Value("${app.default-admin.email}")
    private String defaultAdminEmail;

    public UserResponse getUserById(UUID id) {
        return userMapper.toResponse(findOrThrow(id));
    }

    public Page<UserResponse> getAllUsers(UserSpec spec, Pageable pageable) {
        return userListing.findAll(spec, pageable).map(userMapper::toResponse);
    }

    public boolean isEmailAvailable(String email, UUID excludeId) {
        return excludeId != null
                ? !userRepository.existsByEmailIgnoreCaseAndIdNot(email, excludeId)
                : !userRepository.existsByEmailIgnoreCase(email);
    }

    public boolean isUsernameAvailable(String username, UUID excludeId) {
        return excludeId != null
                ? !userRepository.existsByUsernameAndIdNot(username, excludeId)
                : !userRepository.existsByUsername(username);
    }

    public Page<StaffUserDropdownResponse> getStaffUsers(StaffUserSearchSpec spec, Pageable pageable) {
        Specification<UserEntity> base = hasAccountType(AccountType.STAFF).and(hasStatus(UserStatus.ACTIVE));
        Specification<UserEntity> combined = spec != null ? base.and(spec) : base;
        return userListing.findAll(combined, pageable)
                .map(u -> new StaffUserDropdownResponse(u.getId(), u.getName(), u.getEmail(), u.getUsername()));
    }

    public Page<SupervisorUserDropdownResponse> getSupervisorUsers(SupervisorUserSearchSpec spec, Pageable pageable) {
        Specification<UserEntity> base = hasAccountType(AccountType.SUPERVISOR).and(hasStatus(UserStatus.ACTIVE));
        Specification<UserEntity> combined = spec != null ? base.and(spec) : base;
        return userListing.findAll(combined, pageable)
                .map(u -> new SupervisorUserDropdownResponse(u.getId(), u.getName(), u.getEmail(), u.getUsername()));
    }

    @Transactional
    public UserResponse createSupervisor(CreateUserRequest request) {
        return createUser(request.name(), request.email(), request.username(), AccountType.SUPERVISOR, request.roles());
    }

    @Transactional
    public UserResponse createStaff(CreateUserRequest request) {
        return createUser(request.name(), request.email(), request.username(), AccountType.STAFF, request.roles());
    }

    @Transactional
    public UserResponse toggleActive(UUID userId) {
        User user = findOrThrow(userId);
        User saved = toggleActiveUseCase.execute(user);
        if (saved.getStatus() == UserStatus.INACTIVE) {
            authService.logout(userId);
        }
        return userMapper.toResponse(saved);
    }

    @Transactional
    public UserResponse update(UUID id, UpdateUserRequest request) {
        User user = findOrThrow(id);
        if (user.getEmail().equalsIgnoreCase(defaultAdminEmail)) {
            throw new SystemViolationException("user", "user.default.admin.cannot.be.updated");
        }
        user.setName(request.name().trim());
        user.setEmail(request.email().toLowerCase().trim());
        user.setUsername(request.username());
        if (request.roles() != null) {
            user.setRoles(request.roles());
        }
        User saved = updateUserUseCase.execute(user);
        authService.logout(id);
        return userMapper.toResponse(saved);
    }

    private UserResponse createUser(String name, String email, String username, AccountType accountType, Set<UserRole> roles) {
        User user = User.builder()
                .name(name)
                .email(email.toLowerCase())
                .username(username)
                .accountType(accountType)
                .roles(roles)
                .status(UserStatus.PENDING_EMAIL_VERIFICATION)
                .build();
        User created = createUserUseCase.execute(user);
        String token = authService.issueResetToken(created);
        emailService.sendAccountInvitationEmail(name, email, token, accountType);
        return userMapper.toResponse(created);
    }

    private Specification<UserEntity> hasAccountType(AccountType accountType) {
        return (root, query, cb) -> cb.equal(root.get("accountType"), accountType);
    }

    private Specification<UserEntity> hasStatus(UserStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    private User findOrThrow(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }
}
