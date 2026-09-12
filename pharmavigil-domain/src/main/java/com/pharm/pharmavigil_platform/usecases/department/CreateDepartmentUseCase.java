package com.pharm.pharmavigil_platform.usecases.department;

import com.pharm.pharmavigil_platform.domain.Department;
import com.pharm.pharmavigil_platform.repository.DepartmentRepository;
import com.pharm.pharmavigil_platform.security.IdentityProvider;
import com.pharm.pharmavigil_platform.validators.ValidatorChain;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

@Slf4j
public class CreateDepartmentUseCase {

    private final ValidatorChain<Department> validators;
    private final DepartmentRepository repository;
    private final IdentityProvider identityProvider;

    public CreateDepartmentUseCase(ValidatorChain<Department> validators, DepartmentRepository repository, IdentityProvider identityProvider) {
        this.validators = validators;
        this.repository = repository;
        this.identityProvider = identityProvider;
    }

    public Department execute(Department department) {
        log.info("Creating department with name '{}'", department.getName());
        validators.validate(department).throwExceptionIfViolated();
        String currentUser = identityProvider.getCurrentUser().username();
        Instant now = Instant.now();
        department.setCreatedBy(currentUser);
        department.setLastUpdatedAt(now);
        department.setLastUpdatedBy(currentUser);
        Department created = repository.save(department);
        log.info("Department created successfully with id '{}'", created.getId());
        return created;
    }
}
