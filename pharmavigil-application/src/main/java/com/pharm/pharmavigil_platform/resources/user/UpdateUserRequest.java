package com.pharm.pharmavigil_platform.resources.user;

import com.pharm.pharmavigil_platform.domain.UserRole;

import java.util.Set;

public record UpdateUserRequest(String name, String email, String username, Set<UserRole> roles) {
}
