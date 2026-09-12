package com.pharm.pharmavigil_platform.repository;

import com.pharm.pharmavigil_platform.domain.AccountType;
import com.pharm.pharmavigil_platform.domain.UserRole;

import java.util.Set;

public interface RoleDefinitionProvider {
    Set<UserRole> getAllowedRoles(AccountType accountType);
}
