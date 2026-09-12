package com.pharm.pharmavigil_platform.resources.department;

import java.util.UUID;

public record DepartmentWithBatchesResponse(
        UUID id,
        String name,
        boolean terminalDepartment,
        int standardHoldingTime
) {}
