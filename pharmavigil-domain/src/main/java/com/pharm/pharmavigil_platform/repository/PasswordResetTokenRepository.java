package com.pharm.pharmavigil_platform.repository;

import com.pharm.pharmavigil_platform.domain.PasswordResetToken;

import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenRepository {
    Optional<PasswordResetToken> findByToken(String token);
    PasswordResetToken save(PasswordResetToken token);
    void invalidateActiveTokensByUserId(UUID userId);
}
