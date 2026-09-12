package com.pharm.pharmavigil_platform.resources.product;

import java.time.Instant;
import java.util.UUID;

public record ProductResponse(UUID id, String name, boolean active, Instant createdAt, String createdBy,
                               Instant lastUpdatedAt, String lastUpdatedBy) {}
