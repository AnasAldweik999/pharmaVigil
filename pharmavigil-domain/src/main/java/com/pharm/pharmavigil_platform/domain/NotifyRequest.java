package com.pharm.pharmavigil_platform.domain;

import lombok.Builder;

import java.util.Objects;
import java.util.UUID;

/**
 * The single parameter model for Notifier.send(...) — covers both "notify this one user" and
 * "notify this whole audience" through the same shape, so callers never need to pick between two
 * different methods. Build it with NotifyRequest.builder()..., same as Notification elsewhere in
 * this package; leave specificUserId unset unless audience is SPECIFIC_USER.
 */
public record NotifyRequest(
        NotificationAudience audience,
        UUID specificUserId,
        String type,
        String title,
        String message,
        String entityType,
        String entityId
) {
    @Builder
    public NotifyRequest {
        Objects.requireNonNull(audience, "audience is required");
        Objects.requireNonNull(type, "type is required");
        Objects.requireNonNull(title, "title is required");
        Objects.requireNonNull(message, "message is required");
        if (audience == NotificationAudience.SPECIFIC_USER) {
            Objects.requireNonNull(specificUserId, "specificUserId is required for NotificationAudience.SPECIFIC_USER");
        } else if (specificUserId != null) {
            throw new IllegalArgumentException("specificUserId must be null unless audience is SPECIFIC_USER");
        }
    }
}
