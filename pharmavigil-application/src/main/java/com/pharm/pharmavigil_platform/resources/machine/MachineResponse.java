package com.pharm.pharmavigil_platform.resources.machine;

import java.time.Instant;
import java.util.UUID;

public record MachineResponse(UUID id, String name, boolean active, Instant createdAt, String createdBy,
                               Instant lastUpdatedAt, String lastUpdatedBy) {
}
