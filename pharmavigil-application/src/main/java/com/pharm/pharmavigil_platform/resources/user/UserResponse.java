package com.pharm.pharmavigil_platform.resources.user;

import com.pharm.pharmavigil_platform.domain.AccountType;
import com.pharm.pharmavigil_platform.domain.UserRole;
import com.pharm.pharmavigil_platform.domain.UserStatus;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        String username,
        AccountType accountType,
        Set<UserRole> roles,
        UserStatus status,
        Instant createdAt,
        String createdBy,
        Instant lastUpdatedAt,
        String lastUpdatedBy
) {}
