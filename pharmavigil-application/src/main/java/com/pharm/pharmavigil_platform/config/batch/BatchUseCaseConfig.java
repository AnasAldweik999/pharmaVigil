package com.pharm.pharmavigil_platform.config.batch;

import com.pharm.pharmavigil_platform.domain.Batch;
import com.pharm.pharmavigil_platform.domain.BatchLogEntryDeletion;
import com.pharm.pharmavigil_platform.domain.EffectiveHoldingTime;
import com.pharm.pharmavigil_platform.repository.BatchRepository;
import com.pharm.pharmavigil_platform.repository.DepartmentRepository;
import com.pharm.pharmavigil_platform.repository.MachineRepository;
import com.pharm.pharmavigil_platform.repository.ProductRepository;
import com.pharm.pharmavigil_platform.security.IdentityProvider;
import com.pharm.pharmavigil_platform.usecases.batch.CreateBatchLogEntryUseCase;
import com.pharm.pharmavigil_platform.usecases.batch.DeleteBatchLogEntryUseCase;
import com.pharm.pharmavigil_platform.usecases.batch.EnrichBatchHoldingInfoUseCase;
import com.pharm.pharmavigil_platform.usecases.batch.EnrichBatchLogEntryUseCase;
import com.pharm.pharmavigil_platform.usecases.batch.EvaluateBatchHoldingUseCase;
import com.pharm.pharmavigil_platform.usecases.batch.RejectBatchUseCase;
import com.pharm.pharmavigil_platform.repository.GeneralHoldingThresholdProvider;
import com.pharm.pharmavigil_platform.repository.HoldingAlertEmailProvider;
import com.pharm.pharmavigil_platform.repository.Mailer;
import com.pharm.pharmavigil_platform.repository.Notifier;
import com.pharm.pharmavigil_platform.repository.UserRepository;
import com.pharm.pharmavigil_platform.validators.ValidatorChain;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BatchUseCaseConfig {

    @Bean
    public EnrichBatchLogEntryUseCase enrichBatchLogEntryUseCase(
            DepartmentRepository departmentRepository,
            MachineRepository machineRepository,
            ProductRepository productRepository) {
        return new EnrichBatchLogEntryUseCase(departmentRepository, machineRepository, productRepository);
    }

    @Bean
    public CreateBatchLogEntryUseCase createBatchLogEntryUseCase(
            @Qualifier("batchCreateValidatorChain") ValidatorChain<Batch> batchCreateValidatorChain,
            EnrichBatchLogEntryUseCase enrichBatchLogEntryUseCase,
            BatchRepository batchRepository,
            IdentityProvider identityProvider) {
        return new CreateBatchLogEntryUseCase(batchCreateValidatorChain, enrichBatchLogEntryUseCase, batchRepository, identityProvider);
    }

    @Bean
    public RejectBatchUseCase rejectBatchUseCase(
            @Qualifier("batchRejectValidatorChain") ValidatorChain<Batch> batchRejectValidatorChain,
            BatchRepository batchRepository,
            IdentityProvider identityProvider) {
        return new RejectBatchUseCase(batchRejectValidatorChain, batchRepository, identityProvider);
    }

    @Bean
    public DeleteBatchLogEntryUseCase deleteBatchLogEntryUseCase(
            @Qualifier("batchLogEntryDeletionValidatorChain") ValidatorChain<BatchLogEntryDeletion> batchLogEntryDeletionValidatorChain,
            BatchRepository batchRepository) {
        return new DeleteBatchLogEntryUseCase(batchLogEntryDeletionValidatorChain, batchRepository);
    }

    @Bean
    public EffectiveHoldingTime effectiveHoldingTime() {
        return new EffectiveHoldingTime();
    }

    @Bean
    public EnrichBatchHoldingInfoUseCase enrichBatchHoldingInfoUseCase(
            DepartmentRepository departmentRepository, EffectiveHoldingTime effectiveHoldingTime) {
        return new EnrichBatchHoldingInfoUseCase(departmentRepository, effectiveHoldingTime);
    }

    @Bean
    public EvaluateBatchHoldingUseCase evaluateBatchHoldingUseCase(
            BatchRepository batchRepository,
            DepartmentRepository departmentRepository,
            UserRepository userRepository,
            Notifier notifier,
            Mailer mailer,
            GeneralHoldingThresholdProvider generalHoldingThresholdProvider,
            HoldingAlertEmailProvider holdingAlertEmailProvider,
            EffectiveHoldingTime effectiveHoldingTime) {
        return new EvaluateBatchHoldingUseCase(batchRepository, departmentRepository, userRepository,
                notifier, mailer, generalHoldingThresholdProvider, holdingAlertEmailProvider, effectiveHoldingTime);
    }
}
