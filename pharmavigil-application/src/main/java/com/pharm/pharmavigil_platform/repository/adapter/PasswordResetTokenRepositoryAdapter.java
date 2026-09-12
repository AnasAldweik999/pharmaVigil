package com.pharm.pharmavigil_platform.repository.adapter;

import com.pharm.pharmavigil_platform.domain.PasswordResetToken;
import com.pharm.pharmavigil_platform.mapper.PasswordResetTokenMapper;
import com.pharm.pharmavigil_platform.repository.PasswordResetTokenRepository;
import com.pharm.pharmavigil_platform.repository.jpa.JpaPasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PasswordResetTokenRepositoryAdapter implements PasswordResetTokenRepository {

    private final JpaPasswordResetTokenRepository jpaPasswordResetTokenRepository;
    private final PasswordResetTokenMapper passwordResetTokenMapper;

    @Override
    public Optional<PasswordResetToken> findByToken(String token) {
        return jpaPasswordResetTokenRepository.findByToken(token).map(passwordResetTokenMapper::toDomain);
    }

    @Override
    public PasswordResetToken save(PasswordResetToken token) {
        return passwordResetTokenMapper.toDomain(
                jpaPasswordResetTokenRepository.save(passwordResetTokenMapper.toEntity(token)));
    }

    @Override
    public void invalidateActiveTokensByUserId(UUID userId) {
        jpaPasswordResetTokenRepository.invalidateActiveTokensByUserId(userId);
    }
}
