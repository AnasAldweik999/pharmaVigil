package com.pharm.pharmavigil_platform.resources.stoptype;

import java.time.Instant;
import java.util.UUID;

public record StopTypeResponse(UUID id, String name, boolean active, Instant createdAt, String createdBy,
                                Instant lastUpdatedAt, String lastUpdatedBy) {
}
