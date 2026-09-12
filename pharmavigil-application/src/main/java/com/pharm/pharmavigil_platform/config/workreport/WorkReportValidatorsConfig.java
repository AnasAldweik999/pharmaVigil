package com.pharm.pharmavigil_platform.config.workreport;

import com.pharm.pharmavigil_platform.domain.WorkReport;
import com.pharm.pharmavigil_platform.repository.DepartmentRepository;
import com.pharm.pharmavigil_platform.repository.MachineRepository;
import com.pharm.pharmavigil_platform.repository.ProductRepository;
import com.pharm.pharmavigil_platform.repository.ShiftRepository;
import com.pharm.pharmavigil_platform.repository.StopTypeRepository;
import com.pharm.pharmavigil_platform.util.DurationFormatter;
import com.pharm.pharmavigil_platform.validators.workreport.WorkReportShiftValidator;
import com.pharm.pharmavigil_platform.repository.WorkReportRepository;
import com.pharm.pharmavigil_platform.validators.ValidatorChain;
import com.pharm.pharmavigil_platform.validators.workreport.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class WorkReportValidatorsConfig {

    @Bean
    public WorkReportExistsValidator workReportExistsValidator(WorkReportRepository workReportRepository) {
        return new WorkReportExistsValidator(workReportRepository);
    }

    @Bean
    public WorkReportDateValidator workReportDateValidator() {
        return new WorkReportDateValidator();
    }

    @Bean
    public WorkReportDuplicateValidator workReportDuplicateValidator(WorkReportRepository workReportRepository) {
        return new WorkReportDuplicateValidator(workReportRepository);
    }

    @Bean
    public WorkReportShiftValidator workReportShiftValidator(ShiftRepository shiftRepository) {
        return new WorkReportShiftValidator(shiftRepository);
    }

    @Bean
    public DurationFormatter durationFormatter() {
        return new DurationFormatter();
    }

    @Bean
    public WorkReportDetailsValidator workReportDetailsValidator(
            MachineRepository machineRepository,
            StopTypeRepository stopTypeRepository,
            DepartmentRepository departmentRepository,
            ProductRepository productRepository,
            DurationFormatter durationFormatter,
            @Value("${app.batch-no.regex}") String batchNoRegex) {
        return new WorkReportDetailsValidator(machineRepository, stopTypeRepository, departmentRepository, productRepository, durationFormatter, batchNoRegex);
    }

    @Bean("workReportDeleteValidatorChain")
    public ValidatorChain<WorkReport> workReportDeleteValidatorChain(WorkReportExistsValidator workReportExistsValidator) {
        return new ValidatorChain<>(List.of(workReportExistsValidator));
    }

    @Bean("workReportCreateValidatorChain")
    public ValidatorChain<WorkReport> workReportCreateValidatorChain(
            WorkReportDateValidator workReportDateValidator,
            WorkReportDuplicateValidator workReportDuplicateValidator,
            WorkReportShiftValidator workReportShiftValidator,
            WorkReportDetailsValidator workReportDetailsValidator) {
        return new ValidatorChain<>(List.of(
                workReportDateValidator,
                workReportDuplicateValidator,
                workReportShiftValidator,
                workReportDetailsValidator
        ));
    }
}
