package com.pharm.pharmavigil_platform.resources.shift;

import java.time.Instant;
import java.util.UUID;

public record ShiftResponse(UUID id, String name, boolean active, Instant createdAt, String createdBy,
                             Instant lastUpdatedAt, String lastUpdatedBy) {
}
