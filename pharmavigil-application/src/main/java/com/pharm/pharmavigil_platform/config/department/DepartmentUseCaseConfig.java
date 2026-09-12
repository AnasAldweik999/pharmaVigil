package com.pharm.pharmavigil_platform.config.department;

import com.pharm.pharmavigil_platform.domain.Department;
import com.pharm.pharmavigil_platform.repository.DepartmentRepository;
import com.pharm.pharmavigil_platform.security.IdentityProvider;
import com.pharm.pharmavigil_platform.usecases.department.CreateDepartmentUseCase;
import com.pharm.pharmavigil_platform.usecases.department.ToggleActiveDepartmentUseCase;
import com.pharm.pharmavigil_platform.usecases.department.UpdateDepartmentUseCase;
import com.pharm.pharmavigil_platform.validators.ValidatorChain;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DepartmentUseCaseConfig {

    @Bean
    public CreateDepartmentUseCase createDepartmentUseCase(
            @Qualifier("departmentCreateValidatorChain") ValidatorChain<Department> departmentCreateValidatorChain,
            DepartmentRepository departmentRepository,
            IdentityProvider identityProvider) {
        return new CreateDepartmentUseCase(departmentCreateValidatorChain, departmentRepository, identityProvider);
    }

    @Bean
    public UpdateDepartmentUseCase updateDepartmentUseCase(
            @Qualifier("departmentUpdateValidatorChain") ValidatorChain<Department> departmentUpdateValidatorChain,
            DepartmentRepository departmentRepository,
            IdentityProvider identityProvider) {
        return new UpdateDepartmentUseCase(departmentUpdateValidatorChain, departmentRepository, identityProvider);
    }

    @Bean
    public ToggleActiveDepartmentUseCase toggleActiveDepartmentUseCase(
            @Qualifier("departmentToggleActiveValidatorChain") ValidatorChain<Department> departmentToggleActiveValidatorChain,
            DepartmentRepository departmentRepository,
            IdentityProvider identityProvider) {
        return new ToggleActiveDepartmentUseCase(departmentToggleActiveValidatorChain, departmentRepository, identityProvider);
    }
}
