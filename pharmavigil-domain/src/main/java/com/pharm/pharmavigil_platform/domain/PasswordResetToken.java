package com.pharm.pharmavigil_platform.domain;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetToken {

    private UUID id;
    private User user;
    private String token;
    private Instant expiresAt;
    @Builder.Default
    private boolean used = false;
    private Instant createdAt;
}
