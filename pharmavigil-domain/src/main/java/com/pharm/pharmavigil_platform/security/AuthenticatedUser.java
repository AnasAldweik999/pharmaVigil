package com.pharm.pharmavigil_platform.security;

import java.util.List;
import java.util.UUID;

public record AuthenticatedUser(UUID id, String fullName, String email, String username, String accountType, List<String> roles) {}
