package com.pharm.pharmavigil_platform.config.workreport;

import com.pharm.pharmavigil_platform.domain.WorkReport;
import com.pharm.pharmavigil_platform.repository.DepartmentRepository;
import com.pharm.pharmavigil_platform.repository.MachineRepository;
import com.pharm.pharmavigil_platform.repository.ProductRepository;
import com.pharm.pharmavigil_platform.repository.ShiftRepository;
import com.pharm.pharmavigil_platform.repository.StopTypeRepository;
import com.pharm.pharmavigil_platform.repository.WorkReportRepository;
import com.pharm.pharmavigil_platform.security.IdentityProvider;
import com.pharm.pharmavigil_platform.usecases.workreport.CreateWorkReportUseCase;
import com.pharm.pharmavigil_platform.usecases.workreport.DeleteWorkReportUseCase;
import com.pharm.pharmavigil_platform.usecases.workreport.EnrichWorkReportUseCase;
import com.pharm.pharmavigil_platform.validators.ValidatorChain;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WorkReportUseCaseConfig {

    @Bean
    public DeleteWorkReportUseCase deleteWorkReportUseCase(
            @Qualifier("workReportDeleteValidatorChain") ValidatorChain<WorkReport> workReportDeleteValidatorChain,
            WorkReportRepository workReportRepository) {
        return new DeleteWorkReportUseCase(workReportDeleteValidatorChain, workReportRepository);
    }

    @Bean
    public EnrichWorkReportUseCase enrichWorkReportUseCase(
            ShiftRepository shiftRepository,
            MachineRepository machineRepository,
            DepartmentRepository departmentRepository,
            ProductRepository productRepository,
            StopTypeRepository stopTypeRepository) {
        return new EnrichWorkReportUseCase(shiftRepository, machineRepository, departmentRepository, productRepository, stopTypeRepository);
    }

    @Bean
    public CreateWorkReportUseCase createWorkReportUseCase(
            @Qualifier("workReportCreateValidatorChain") ValidatorChain<WorkReport> workReportCreateValidatorChain,
            EnrichWorkReportUseCase enrichWorkReportUseCase,
            WorkReportRepository workReportRepository,
            IdentityProvider identityProvider) {
        return new CreateWorkReportUseCase(workReportCreateValidatorChain, enrichWorkReportUseCase, workReportRepository, identityProvider);
    }
}
