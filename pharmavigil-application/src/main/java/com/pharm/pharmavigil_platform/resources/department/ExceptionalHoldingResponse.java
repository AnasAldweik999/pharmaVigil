package com.pharm.pharmavigil_platform.resources.department;

import java.util.UUID;

public record ExceptionalHoldingResponse(UUID id, UUID productId, int holdingTimeDays) {}
