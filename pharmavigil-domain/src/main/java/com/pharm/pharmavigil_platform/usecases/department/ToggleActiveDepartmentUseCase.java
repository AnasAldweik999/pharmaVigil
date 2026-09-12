package com.pharm.pharmavigil_platform.usecases.department;

import com.pharm.pharmavigil_platform.domain.Department;
import com.pharm.pharmavigil_platform.repository.DepartmentRepository;
import com.pharm.pharmavigil_platform.security.IdentityProvider;
import com.pharm.pharmavigil_platform.validators.ValidatorChain;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

@Slf4j
public class ToggleActiveDepartmentUseCase {

    private final ValidatorChain<Department> validators;
    private final DepartmentRepository repository;
    private final IdentityProvider identityProvider;

    public ToggleActiveDepartmentUseCase(ValidatorChain<Department> validators, DepartmentRepository repository, IdentityProvider identityProvider) {
        this.validators = validators;
        this.repository = repository;
        this.identityProvider = identityProvider;
    }

    public Department execute(Department department) {
        log.info("Toggling active status for department with id '{}'", department.getId());
        validators.validate(department).throwExceptionIfViolated();
        department.setActive(!department.isActive());
        department.setLastUpdatedAt(Instant.now());
        department.setLastUpdatedBy(identityProvider.getCurrentUser().username());
        Department saved = repository.save(department);
        log.info("Department '{}' active status changed to '{}'", department.getId(), saved.isActive());
        return saved;
    }
}
