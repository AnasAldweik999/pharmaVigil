package com.pharm.pharmavigil_platform.security;

import com.pharm.pharmavigil_platform.domain.UserRole;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Aspect
@Component
public class RequiresRoleAspect {

    @Around("@annotation(requiresRole)")
    public Object enforce(ProceedingJoinPoint pjp, RequiresRole requiresRole) throws Throwable {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("access.denied");
        }

        Set<String> authorities = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        boolean hasAnyRequiredRole = Arrays.stream(requiresRole.value())
                .map(UserRole::name)
                .anyMatch(authorities::contains);

        if (!hasAnyRequiredRole) {
            throw new AccessDeniedException("access.denied");
        }

        return pjp.proceed();
    }
}
