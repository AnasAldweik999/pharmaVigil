package com.pharm.pharmavigil_platform.resources.department;

import java.util.UUID;

public record ExceptionalHoldingRequest(UUID productId, int holdingTimeDays) {}
