package com.pharm.pharmavigil_platform.resources.supervisor.dashboard;

import java.util.UUID;

public record StopProductRow(UUID productId, String productName, String batchNo, String consignee,
                              String completedStages) {}
