package com.pharm.pharmavigil_platform.usecases.department;

import com.pharm.pharmavigil_platform.domain.Department;
import com.pharm.pharmavigil_platform.repository.DepartmentRepository;
import com.pharm.pharmavigil_platform.security.IdentityProvider;
import com.pharm.pharmavigil_platform.validators.ValidatorChain;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

@Slf4j
public class UpdateDepartmentUseCase {

    private final ValidatorChain<Department> validators;
    private final DepartmentRepository repository;
    private final IdentityProvider identityProvider;

    public UpdateDepartmentUseCase(ValidatorChain<Department> validators, DepartmentRepository repository, IdentityProvider identityProvider) {
        this.validators = validators;
        this.repository = repository;
        this.identityProvider = identityProvider;
    }

    public Department execute(Department department) {
        log.info("Updating department with id '{}'", department.getId());
        validators.validate(department).throwExceptionIfViolated();
        department.setLastUpdatedAt(Instant.now());
        department.setLastUpdatedBy(identityProvider.getCurrentUser().username());
        Department updated = repository.save(department);
        log.info("Department updated successfully with id '{}'", updated.getId());
        return updated;
    }
}
