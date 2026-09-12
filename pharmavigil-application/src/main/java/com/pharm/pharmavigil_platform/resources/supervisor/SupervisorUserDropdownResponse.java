package com.pharm.pharmavigil_platform.resources.supervisor;

import java.util.UUID;

public record SupervisorUserDropdownResponse(UUID id, String name, String email, String username) {}
