package com.pharm.pharmavigil_platform.service;

import com.pharm.pharmavigil_platform.domain.AccountType;
import com.pharm.pharmavigil_platform.domain.RefreshToken;
import com.pharm.pharmavigil_platform.domain.User;
import com.pharm.pharmavigil_platform.domain.UserStatus;
import com.pharm.pharmavigil_platform.repository.PasswordResetTokenRepository;
import com.pharm.pharmavigil_platform.repository.RefreshTokenRepository;
import com.pharm.pharmavigil_platform.repository.UserRepository;
import com.pharm.pharmavigil_platform.resources.auth.RefreshTokenRequest;
import com.pharm.pharmavigil_platform.security.IdentityProvider;
import com.pharm.pharmavigil_platform.security.JwtTokenProvider;
import com.pharm.pharmavigil_platform.validators.exceptions.SystemViolationException;
import com.pharm.pharmavigil_platform.validators.user.UserUsernameUniqueValidator;
import com.pharm.pharmavigil_platform.validators.user.UserUsernameValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    private UserRepository userRepository;
    private RefreshTokenRepository refreshTokenRepository;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        refreshTokenRepository = mock(RefreshTokenRepository.class);

        authService = new AuthService(
                mock(AuthenticationManager.class),
                userRepository,
                refreshTokenRepository,
                mock(PasswordResetTokenRepository.class),
                mock(JwtTokenProvider.class),
                mock(PasswordEncoder.class),
                mock(IdentityProvider.class),
                mock(EmailService.class),
                mock(UserUsernameValidator.class),
                mock(UserUsernameUniqueValidator.class)
        );
        ReflectionTestUtils.setField(authService, "refreshTokenExpirationMs", 604_800_000L);
    }

    private static String sha256(String raw) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(raw.getBytes());
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private User activeStaffUser(UUID userId) {
        return User.builder()
                .id(userId)
                .name("Jane")
                .email("jane@example.com")
                .username("jane")
                .accountType(AccountType.STAFF)
                .status(UserStatus.ACTIVE)
                .build();
    }

    private RefreshToken sessionFor(UUID userId, String rawToken) {
        return RefreshToken.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .tokenHash(sha256(rawToken))
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
    }

    @Test
    void refreshingOneSessionDoesNotInvalidateAnotherSessionOfTheSameUser() {
        UUID userId = UUID.randomUUID();
        User user = activeStaffUser(userId);

        String rawTokenA = "session-a-raw-token";
        String rawTokenB = "session-b-raw-token";
        RefreshToken sessionA = sessionFor(userId, rawTokenA);
        RefreshToken sessionB = sessionFor(userId, rawTokenB);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(inv -> inv.getArgument(0));
        when(refreshTokenRepository.findByTokenHash(sha256(rawTokenA))).thenReturn(Optional.of(sessionA));
        when(refreshTokenRepository.findByTokenHash(sha256(rawTokenB))).thenReturn(Optional.of(sessionB));

        authService.refresh(new RefreshTokenRequest(rawTokenA));

        assertThatCode(() -> authService.refresh(new RefreshTokenRequest(rawTokenB)))
                .doesNotThrowAnyException();
    }

    @Test
    void refreshWithUnknownTokenThrowsInvalid() {
        when(refreshTokenRepository.findByTokenHash(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.refresh(new RefreshTokenRequest("not-a-real-token")))
                .isInstanceOf(SystemViolationException.class);
    }
}
