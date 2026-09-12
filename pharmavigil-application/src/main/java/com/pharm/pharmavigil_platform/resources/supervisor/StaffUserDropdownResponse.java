package com.pharm.pharmavigil_platform.resources.supervisor;

import java.util.UUID;

public record StaffUserDropdownResponse(UUID id, String name, String email, String username) {}
