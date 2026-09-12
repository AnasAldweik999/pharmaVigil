package com.pharm.pharmavigil_platform.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharm.pharmavigil_platform.domain.AccountType;
import com.pharm.pharmavigil_platform.domain.UserRole;
import com.pharm.pharmavigil_platform.repository.RoleDefinitionProvider;
import com.pharm.pharmavigil_platform.resources.role.RoleResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RolesProvider implements RoleDefinitionProvider {

    private final ObjectMapper objectMapper;
    private List<RoleResponse> roles;

    @PostConstruct
    void load() throws IOException {
        roles = objectMapper.readValue(
                new ClassPathResource("roles.json").getInputStream(),
                new TypeReference<>() {}
        );
    }

    public List<RoleResponse> getAll() {
        return roles;
    }

    @Override
    public Set<UserRole> getAllowedRoles(AccountType accountType) {
        return roles.stream()
                .filter(r -> accountType.name().equals(r.accountType()))
                .map(r -> UserRole.valueOf(r.key()))
                .collect(Collectors.toSet());
    }
}
