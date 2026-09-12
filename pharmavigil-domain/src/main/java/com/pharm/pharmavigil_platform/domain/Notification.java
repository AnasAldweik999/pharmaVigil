package com.pharm.pharmavigil_platform.domain;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    private UUID id;
    private UUID recipientUserId;
    private String type;
    private String title;
    private String message;
    private String entityType;
    private String entityId;
    @Builder.Default
    private boolean read = false;
    private Instant createdAt;
    private Instant readAt;
}
