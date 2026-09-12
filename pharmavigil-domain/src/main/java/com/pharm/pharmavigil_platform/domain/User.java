package com.pharm.pharmavigil_platform.domain;

import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private UUID id;
    private String name;
    private String email;
    private String username;
    private String passwordHash;
    private AccountType accountType;
    @Builder.Default
    private Set<UserRole> roles = new HashSet<>();
    @Builder.Default
    private UserStatus status = UserStatus.PENDING_EMAIL_VERIFICATION;
    private Instant loggedOutAt;
    private Instant createdAt;
    private String createdBy;
    private Instant lastUpdatedAt;
    private String lastUpdatedBy;
}
