package com.pharm.pharmavigil_platform.repository;

import com.pharm.pharmavigil_platform.domain.RefreshToken;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository {
    RefreshToken save(RefreshToken refreshToken);
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    void deleteById(UUID id);
    void deleteAllByUserId(UUID userId);
}
