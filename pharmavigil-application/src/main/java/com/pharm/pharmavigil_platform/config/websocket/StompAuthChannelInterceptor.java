package com.pharm.pharmavigil_platform.config.websocket;

import com.pharm.pharmavigil_platform.domain.UserStatus;
import com.pharm.pharmavigil_platform.repository.UserRepository;
import com.pharm.pharmavigil_platform.security.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.UUID;

/**
 * Authenticates the STOMP CONNECT frame using the same JWT the REST API uses (sent as a native
 * "Authorization" STOMP header, not an HTTP header — the HTTP handshake for /ws itself is
 * permitted anonymously in SecurityConfig, since this is where the real auth gate lives instead).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        // MUST use getAccessor (not StompHeaderAccessor.wrap) here: wrap() creates a detached
        // copy of the headers, so mutating it (accessor.setUser(...) below) would never be
        // visible on the message actually returned to the channel — Spring would then have no
        // Principal registered for this session, and convertAndSendToUser(...) could never find
        // it. getAccessor() returns the same mutable accessor instance backing this message's
        // headers, so the mutation is real.
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = resolveToken(accessor);
            if (token == null || !jwtTokenProvider.isValid(token)) {
                throw new AccessDeniedException("Missing or invalid token for WebSocket connection");
            }

            Claims claims = jwtTokenProvider.extractClaims(token);
            UUID userId = UUID.fromString(claims.getSubject());
            var issuedAt = claims.getIssuedAt();

            boolean isActive = userRepository.findById(userId)
                    .map(u -> u.getStatus() == UserStatus.ACTIVE
                            && (u.getLoggedOutAt() == null
                                || issuedAt == null
                                || issuedAt.toInstant().isAfter(u.getLoggedOutAt())))
                    .orElse(false);
            if (!isActive) {
                throw new AccessDeniedException("User is not active");
            }

            accessor.setUser(new StompPrincipal(userId.toString()));
        }
        return message;
    }

    private String resolveToken(StompHeaderAccessor accessor) {
        String header = accessor.getFirstNativeHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
