package com.pharm.pharmavigil_platform.resources.auth;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        String accountType,
        long expiresIn,
        String name,
        String email,
        String username,
        boolean requiresUsernameSetup,
        String setupToken
) {
}
