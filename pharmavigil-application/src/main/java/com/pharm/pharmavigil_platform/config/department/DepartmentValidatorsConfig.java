package com.pharm.pharmavigil_platform.config.department;

import com.pharm.pharmavigil_platform.domain.Department;
import com.pharm.pharmavigil_platform.repository.AllowedUnitsProvider;
import com.pharm.pharmavigil_platform.repository.DepartmentRepository;
import com.pharm.pharmavigil_platform.repository.MachineRepository;
import com.pharm.pharmavigil_platform.repository.ProductRepository;
import com.pharm.pharmavigil_platform.repository.UserRepository;
import com.pharm.pharmavigil_platform.validators.ValidatorChain;
import com.pharm.pharmavigil_platform.validators.department.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DepartmentValidatorsConfig {

    @Bean
    public DepartmentNameValidator departmentNameValidator() {
        return new DepartmentNameValidator();
    }

    @Bean
    public DepartmentNameUniquenessValidator departmentNameUniquenessValidator(DepartmentRepository departmentRepository) {
        return new DepartmentNameUniquenessValidator(departmentRepository);
    }

    @Bean
    public DepartmentNameNotTakenByOthersValidator departmentNameNotTakenByOthersValidator(DepartmentRepository departmentRepository) {
        return new DepartmentNameNotTakenByOthersValidator(departmentRepository);
    }

    @Bean
    public DepartmentStagesValidator departmentStagesValidator() {
        return new DepartmentStagesValidator();
    }

    @Bean
    public DepartmentUnitsValidator departmentUnitsValidator(AllowedUnitsProvider allowedUnitsProvider) {
        return new DepartmentUnitsValidator(allowedUnitsProvider);
    }

    @Bean
    public DepartmentSupervisorsValidator departmentSupervisorsValidator(UserRepository userRepository) {
        return new DepartmentSupervisorsValidator(userRepository);
    }

    @Bean
    public DepartmentHoldingValidator departmentHoldingValidator(ProductRepository productRepository) {
        return new DepartmentHoldingValidator(productRepository);
    }

    @Bean
    public DepartmentExistsValidator departmentExistsValidator(DepartmentRepository departmentRepository) {
        return new DepartmentExistsValidator(departmentRepository);
    }

    @Bean
    public DepartmentMachinesValidator departmentMachinesValidator(MachineRepository machineRepository,
                                                                   DepartmentRepository departmentRepository) {
        return new DepartmentMachinesValidator(machineRepository, departmentRepository);
    }

    @Bean
    public ValidatorChain<Department> departmentCreateValidatorChain(
            DepartmentNameValidator departmentNameValidator,
            DepartmentNameUniquenessValidator departmentNameUniquenessValidator,
            DepartmentStagesValidator departmentStagesValidator,
            DepartmentUnitsValidator departmentUnitsValidator,
            DepartmentMachinesValidator departmentMachinesValidator,
            DepartmentSupervisorsValidator departmentSupervisorsValidator,
            DepartmentHoldingValidator departmentHoldingValidator) {
        return new ValidatorChain<>(List.of(
                departmentNameValidator,
                departmentNameUniquenessValidator,
                departmentStagesValidator,
                departmentUnitsValidator,
                departmentMachinesValidator,
                departmentSupervisorsValidator,
                departmentHoldingValidator));
    }

    @Bean
    public ValidatorChain<Department> departmentUpdateValidatorChain(
            DepartmentExistsValidator departmentExistsValidator,
            DepartmentNameNotTakenByOthersValidator departmentNameNotTakenByOthersValidator,
            DepartmentStagesValidator departmentStagesValidator,
            DepartmentUnitsValidator departmentUnitsValidator,
            DepartmentMachinesValidator departmentMachinesValidator,
            DepartmentSupervisorsValidator departmentSupervisorsValidator,
            DepartmentHoldingValidator departmentHoldingValidator) {
        return new ValidatorChain<>(List.of(
                departmentExistsValidator,
                departmentNameNotTakenByOthersValidator,
                departmentStagesValidator,
                departmentUnitsValidator,
                departmentMachinesValidator,
                departmentSupervisorsValidator,
                departmentHoldingValidator));
    }

    @Bean
    public ValidatorChain<Department> departmentToggleActiveValidatorChain(DepartmentExistsValidator departmentExistsValidator) {
        return new ValidatorChain<>(List.of(departmentExistsValidator));
    }
}
