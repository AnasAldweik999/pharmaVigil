package com.pharm.pharmavigil_platform.resources.notification;

import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        String type,
        String title,
        String message,
        String entityType,
        String entityId,
        boolean read,
        Instant createdAt,
        Instant readAt
) {}
