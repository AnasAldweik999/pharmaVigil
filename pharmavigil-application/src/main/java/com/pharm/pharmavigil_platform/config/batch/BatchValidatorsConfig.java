package com.pharm.pharmavigil_platform.config.batch;

import com.pharm.pharmavigil_platform.domain.Batch;
import com.pharm.pharmavigil_platform.domain.BatchLogEntryDeletion;
import com.pharm.pharmavigil_platform.repository.DepartmentRepository;
import com.pharm.pharmavigil_platform.repository.MachineRepository;
import com.pharm.pharmavigil_platform.repository.ProductRepository;
import com.pharm.pharmavigil_platform.validators.ValidatorChain;
import com.pharm.pharmavigil_platform.validators.batch.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class BatchValidatorsConfig {

    @Bean
    public BatchLogEntryDetailsValidator batchLogEntryDetailsValidator(
            @Value("${app.batch-no.regex}") String batchNoRegex) {
        return new BatchLogEntryDetailsValidator(batchNoRegex);
    }

    @Bean
    public BatchProductExistsValidator batchProductExistsValidator(ProductRepository productRepository) {
        return new BatchProductExistsValidator(productRepository);
    }

    @Bean
    public BatchDepartmentExistsValidator batchDepartmentExistsValidator(DepartmentRepository departmentRepository) {
        return new BatchDepartmentExistsValidator(departmentRepository);
    }

    @Bean
    public BatchMachineExistsValidator batchMachineExistsValidator(MachineRepository machineRepository) {
        return new BatchMachineExistsValidator(machineRepository);
    }

    @Bean
    public BatchLogEntryMachineDepartmentValidator batchLogEntryMachineDepartmentValidator(DepartmentRepository departmentRepository) {
        return new BatchLogEntryMachineDepartmentValidator(departmentRepository);
    }

    @Bean
    public BatchNotFinalizedValidator batchNotFinalizedValidator() {
        return new BatchNotFinalizedValidator();
    }

    @Bean
    public BatchProductMatchValidator batchProductMatchValidator() {
        return new BatchProductMatchValidator();
    }

    @Bean
    public BatchLogEntryDuplicateMachineValidator batchLogEntryDuplicateMachineValidator(DepartmentRepository departmentRepository) {
        return new BatchLogEntryDuplicateMachineValidator(departmentRepository);
    }

    @Bean
    public BatchLogEntryDepartmentRevisitValidator batchLogEntryDepartmentRevisitValidator(DepartmentRepository departmentRepository) {
        return new BatchLogEntryDepartmentRevisitValidator(departmentRepository);
    }

    @Bean
    public BatchLogEntryDateTimeValidator batchLogEntryDateTimeValidator() {
        return new BatchLogEntryDateTimeValidator();
    }

    @Bean
    public BatchCompletableValidator batchCompletableValidator(DepartmentRepository departmentRepository) {
        return new BatchCompletableValidator(departmentRepository);
    }

    @Bean
    public BatchRejectableValidator batchRejectableValidator() {
        return new BatchRejectableValidator();
    }

    @Bean
    public RejectReasonValidator rejectReasonValidator() {
        return new RejectReasonValidator();
    }

    @Bean
    public RejectLineClearanceRequiredValidator rejectLineClearanceRequiredValidator() {
        return new RejectLineClearanceRequiredValidator();
    }

    @Bean
    public BatchExistsForDeletionValidator batchExistsForDeletionValidator() {
        return new BatchExistsForDeletionValidator();
    }

    @Bean
    public BatchLogEntryExistsValidator batchLogEntryExistsValidator() {
        return new BatchLogEntryExistsValidator();
    }

    @Bean
    public BatchLogEntryIsLastValidator batchLogEntryIsLastValidator() {
        return new BatchLogEntryIsLastValidator();
    }

    @Bean
    public ValidatorChain<Batch> batchCreateValidatorChain(
            BatchLogEntryDetailsValidator batchLogEntryDetailsValidator,
            BatchProductExistsValidator batchProductExistsValidator,
            BatchDepartmentExistsValidator batchDepartmentExistsValidator,
            BatchMachineExistsValidator batchMachineExistsValidator,
            BatchLogEntryMachineDepartmentValidator batchLogEntryMachineDepartmentValidator,
            BatchNotFinalizedValidator batchNotFinalizedValidator,
            BatchProductMatchValidator batchProductMatchValidator,
            BatchLogEntryDuplicateMachineValidator batchLogEntryDuplicateMachineValidator,
            BatchLogEntryDepartmentRevisitValidator batchLogEntryDepartmentRevisitValidator,
            BatchLogEntryDateTimeValidator batchLogEntryDateTimeValidator,
            BatchCompletableValidator batchCompletableValidator) {
        return new ValidatorChain<>(List.of(
                batchLogEntryDetailsValidator,
                batchProductExistsValidator,
                batchDepartmentExistsValidator,
                batchMachineExistsValidator,
                batchLogEntryMachineDepartmentValidator,
                batchNotFinalizedValidator,
                batchProductMatchValidator,
                batchLogEntryDuplicateMachineValidator,
                batchLogEntryDepartmentRevisitValidator,
                batchLogEntryDateTimeValidator,
                batchCompletableValidator));
    }

    @Bean
    public ValidatorChain<Batch> batchRejectValidatorChain(
            BatchRejectableValidator batchRejectableValidator,
            RejectReasonValidator rejectReasonValidator,
            RejectLineClearanceRequiredValidator rejectLineClearanceRequiredValidator,
            BatchLogEntryDateTimeValidator batchLogEntryDateTimeValidator) {
        return new ValidatorChain<>(List.of(
                batchRejectableValidator,
                rejectReasonValidator,
                rejectLineClearanceRequiredValidator,
                batchLogEntryDateTimeValidator));
    }

    @Bean
    public ValidatorChain<BatchLogEntryDeletion> batchLogEntryDeletionValidatorChain(
            BatchExistsForDeletionValidator batchExistsForDeletionValidator,
            BatchLogEntryExistsValidator batchLogEntryExistsValidator,
            BatchLogEntryIsLastValidator batchLogEntryIsLastValidator) {
        return new ValidatorChain<>(List.of(
                batchExistsForDeletionValidator,
                batchLogEntryExistsValidator,
                batchLogEntryIsLastValidator));
    }
}
