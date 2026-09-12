package com.pharm.pharmavigil_platform.service;

import com.pharm.pharmavigil_platform.domain.PasswordResetToken;
import com.pharm.pharmavigil_platform.domain.RefreshToken;
import com.pharm.pharmavigil_platform.domain.User;
import com.pharm.pharmavigil_platform.domain.UserStatus;
import com.pharm.pharmavigil_platform.repository.PasswordResetTokenRepository;
import com.pharm.pharmavigil_platform.repository.RefreshTokenRepository;
import com.pharm.pharmavigil_platform.repository.UserRepository;
import com.pharm.pharmavigil_platform.resources.auth.*;
import com.pharm.pharmavigil_platform.security.AuthenticatedUser;
import com.pharm.pharmavigil_platform.security.IdentityProvider;
import com.pharm.pharmavigil_platform.security.JwtTokenProvider;
import com.pharm.pharmavigil_platform.validators.ValidatorChain;
import com.pharm.pharmavigil_platform.validators.exceptions.SystemViolationException;
import com.pharm.pharmavigil_platform.validators.user.UserUsernameUniqueValidator;
import com.pharm.pharmavigil_platform.validators.user.UserUsernameValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final IdentityProvider identityProvider;
    private final EmailService emailService;
    private final UserUsernameValidator userUsernameValidator;
    private final UserUsernameUniqueValidator userUsernameUniqueValidator;

    @Value("${app.jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.usernameOrEmail())
                .or(() -> userRepository.findByUsername(request.usernameOrEmail()))
                .orElseThrow(() -> new SystemViolationException("credentials", "credentials.invalid"));

        if (user.getStatus() == UserStatus.PENDING_EMAIL_VERIFICATION) {
            throw new SystemViolationException("account", "account.password.not.set");
        }
        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new SystemViolationException("account", "account.deactivated");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), request.password())
            );
        } catch (BadCredentialsException e) {
            throw new SystemViolationException("credentials", "credentials.invalid");
        }

        if (!user.getAccountType().name().equals(request.portalType())) {
            throw new SystemViolationException("credentials", "credentials.portal.access.denied");
        }

        if (user.getUsername() == null || user.getUsername().isBlank()) {
            passwordResetTokenRepository.invalidateActiveTokensByUserId(user.getId());
            String setupToken = issueResetToken(user);
            return new LoginResponse(null, null, user.getAccountType().name(), 0,
                    user.getName(), user.getEmail(), null, true, setupToken);
        }

        return buildFullLoginResponse(user);
    }

    @Transactional
    public LoginResponse refresh(RefreshTokenRequest request) {
        String hash = sha256(request.refreshToken());
        RefreshToken session = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new SystemViolationException("refresh.token", "refresh.token.invalid"));

        if (Instant.now().isAfter(session.getExpiresAt())) {
            refreshTokenRepository.deleteById(session.getId());
            throw new SystemViolationException("refresh.token", "refresh.token.expired");
        }

        User user = userRepository.findById(session.getUserId())
                .orElseThrow(() -> new SystemViolationException("refresh.token", "refresh.token.invalid"));

        String newRefreshToken = rotateSessionToken(session);
        return buildFullLoginResponse(user, newRefreshToken);
    }

    @Transactional
    public LoginResponse setupUsername(SetupUsernameRequest request) {
        PasswordResetToken setupToken = passwordResetTokenRepository.findByToken(request.setupToken())
                .orElseThrow(() -> new SystemViolationException("token", "token.invalid"));

        if (setupToken.isUsed()) {
            throw new SystemViolationException("token", "token.already.used");
        }
        if (Instant.now().isAfter(setupToken.getExpiresAt())) {
            throw new SystemViolationException("token", "token.expired");
        }

        new ValidatorChain<>(List.of(userUsernameValidator, userUsernameUniqueValidator))
                .validate(User.builder().username(request.username()).build())
                .throwExceptionIfViolated();

        User user = setupToken.getUser();
        user.setUsername(request.username());
        userRepository.save(user);

        setupToken.setUsed(true);
        passwordResetTokenRepository.save(setupToken);

        return buildFullLoginResponse(user);
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        userRepository.findByEmailIgnoreCase(request.email()).ifPresent(user -> {
            String token = issueResetToken(user);
            emailService.sendPasswordResetEmail(user.getEmail(), token, user.getAccountType());
        });
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.token())
                .orElseThrow(() -> new SystemViolationException("token", "token.invalid"));

        if (resetToken.isUsed()) {
            throw new SystemViolationException("token", "token.already.used");
        }
        if (Instant.now().isAfter(resetToken.getExpiresAt())) {
            throw new SystemViolationException("token", "token.expired");
        }

        User user = resetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
        clearAllSessions(user);
    }

    @Transactional
    public void logout() {
        AuthenticatedUser currentUser = identityProvider.getCurrentUser();
        if (currentUser != null) logout(currentUser.id());
    }

    @Transactional
    public void logout(UUID userId) {
        userRepository.findById(userId).ifPresent(this::clearAllSessions);
    }

    public String issueResetToken(User user) {
        String token = UUID.randomUUID().toString();
        passwordResetTokenRepository.save(PasswordResetToken.builder()
                .user(user)
                .token(token)
                .expiresAt(Instant.now().plusSeconds(3600))
                .build());
        return token;
    }

    private LoginResponse buildFullLoginResponse(User user) {
        return buildFullLoginResponse(user, issueSessionToken(user));
    }

    private LoginResponse buildFullLoginResponse(User user, String refreshToken) {
        List<String> roleNames = user.getRoles().stream().map(r -> r.name()).toList();
        String accessToken = jwtTokenProvider.generateAccessToken(
                user.getId(), user.getName(), user.getEmail(), user.getUsername(), user.getAccountType().name(), roleNames);
        return new LoginResponse(accessToken, refreshToken, user.getAccountType().name(), 900,
                user.getName(), user.getEmail(), user.getUsername(), false, null);
    }

    private String issueSessionToken(User user) {
        String raw = UUID.randomUUID().toString();
        refreshTokenRepository.save(RefreshToken.builder()
                .userId(user.getId())
                .tokenHash(sha256(raw))
                .expiresAt(Instant.now().plusMillis(refreshTokenExpirationMs))
                .build());
        return raw;
    }

    private String rotateSessionToken(RefreshToken session) {
        String raw = UUID.randomUUID().toString();
        session.setTokenHash(sha256(raw));
        session.setExpiresAt(Instant.now().plusMillis(refreshTokenExpirationMs));
        refreshTokenRepository.save(session);
        return raw;
    }

    private void clearAllSessions(User user) {
        refreshTokenRepository.deleteAllByUserId(user.getId());
        user.setLoggedOutAt(Instant.now());
        userRepository.save(user);
    }

    private String sha256(String input) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256")
                    .digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }
}
