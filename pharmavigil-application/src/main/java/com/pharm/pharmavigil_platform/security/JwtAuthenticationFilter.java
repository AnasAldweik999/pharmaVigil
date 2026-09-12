package com.pharm.pharmavigil_platform.security;

import com.pharm.pharmavigil_platform.domain.UserStatus;
import com.pharm.pharmavigil_platform.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain) throws ServletException, IOException {

        String token = resolveToken(request);

        if (StringUtils.hasText(token) && jwtTokenProvider.isValid(token)) {
            Claims claims = jwtTokenProvider.extractClaims(token);
            UUID userId = UUID.fromString(claims.getSubject());
            String accountType = claims.get("accountType", String.class);
            String username = claims.get("username", String.class);
            String fullName = claims.get("name", String.class);
            List<String> roles = claims.get("roles", List.class);

            Date issuedAt = claims.getIssuedAt();
            boolean isActive = userRepository.findById(userId)
                    .map(u -> u.getStatus() == UserStatus.ACTIVE
                            && (u.getLoggedOutAt() == null
                                || issuedAt == null
                                || issuedAt.toInstant().isAfter(u.getLoggedOutAt())))
                    .orElse(false);

            if (isActive) {
                List<SimpleGrantedAuthority> authorities = roles == null ? List.of()
                        : roles.stream().map(SimpleGrantedAuthority::new).toList();

                var principal = new AuthenticatedUser(
                        userId,
                        fullName,
                        claims.get("email", String.class),
                        username,
                        accountType,
                        roles != null ? roles : List.of()
                );
                var auth = new UsernamePasswordAuthenticationToken(principal, null, authorities);
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        chain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        return (StringUtils.hasText(header) && header.startsWith("Bearer "))
                ? header.substring(7)
                : null;
    }
}
