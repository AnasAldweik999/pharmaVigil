package com.pharm.pharmavigil_platform.domain;

import java.util.UUID;

/**
 * Resolves whether a department has a product-specific exceptional holding time configured,
 * overriding its standard holding time for that product only. Shared by
 * EnrichBatchHoldingInfoUseCase (per-batch API display) and EvaluateBatchHoldingUseCase (the
 * scheduled alert job) so both use the exact same resolution — injected as a plain collaborator
 * rather than called as a static utility.
 */
public class EffectiveHoldingTime {

    public ExceptionalHolding findExceptional(Department department, UUID productId) {
        if (department.getExceptionalHoldings() == null) return null;
        return department.getExceptionalHoldings().stream()
                .filter(h -> h.getProductId().equals(productId))
                .findFirst()
                .orElse(null);
    }
}
