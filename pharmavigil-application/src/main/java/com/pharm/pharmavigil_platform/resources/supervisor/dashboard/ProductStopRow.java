package com.pharm.pharmavigil_platform.resources.supervisor.dashboard;

import java.util.UUID;

public record ProductStopRow(UUID stopTypeId, String stopTypeName, String duration, String note) {}
