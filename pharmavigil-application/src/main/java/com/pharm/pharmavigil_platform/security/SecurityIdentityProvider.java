package com.pharm.pharmavigil_platform.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityIdentityProvider implements IdentityProvider {

    @Override
    public AuthenticatedUser getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AuthenticatedUser)) return null;
        return (AuthenticatedUser) auth.getPrincipal();
    }
}
