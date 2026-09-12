package com.pharm.pharmavigil_platform.usecases.batch;

import com.pharm.pharmavigil_platform.domain.Batch;
import com.pharm.pharmavigil_platform.domain.BatchLogEntry;
import com.pharm.pharmavigil_platform.domain.BatchStatus;
import com.pharm.pharmavigil_platform.repository.BatchRepository;
import com.pharm.pharmavigil_platform.security.IdentityProvider;
import com.pharm.pharmavigil_platform.validators.ValidatorChain;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Comparator;

@Slf4j
public class RejectBatchUseCase {

    private final ValidatorChain<Batch> validators;
    private final BatchRepository repository;
    private final IdentityProvider identityProvider;

    public RejectBatchUseCase(ValidatorChain<Batch> validators, BatchRepository repository, IdentityProvider identityProvider) {
        this.validators = validators;
        this.repository = repository;
        this.identityProvider = identityProvider;
    }

    public Batch execute(Batch batch, String reason, LocalDateTime lineClearanceAt) {
        log.info("Rejecting batch '{}'", batch.getBatchNo());
        BatchLogEntry current = latestEntry(batch);

        BatchLogEntry rejectionEntry = BatchLogEntry.builder()
                .departmentId(current.getDepartmentId())
                .departmentName(current.getDepartmentName())
                .terminalDepartment(current.isTerminalDepartment())
                // The batch's derived current machine, not current.getMachineId() (the raw latest
                // entry's own field) — they can differ when a same-timestamp entry ties with an
                // earlier one (see CreateBatchLogEntryUseCase for why the submitted entry, not the
                // max-lineClearanceAt one, is authoritative for "current").
                .machineId(batch.getCurrentMachineId())
                .machineName(batch.getCurrentMachineName())
                .productId(current.getProductId())
                .productName(current.getProductName())
                .lineClearanceAt(lineClearanceAt)
                .rejected(true)
                .rejectedReason(reason)
                .build();
        batch.getEntries().add(rejectionEntry);

        validators.validate(batch).throwExceptionIfViolated();

        var currentUser = identityProvider.getCurrentUser();
        rejectionEntry.setCreatedBy(currentUser.username());
        rejectionEntry.setCreatedByFullName(currentUser.fullName());
        batch.setStatus(BatchStatus.REJECTED);
        batch.setLastUpdatedAt(Instant.now());
        batch.setLastUpdatedBy(currentUser.username());
        batch.setLastUpdatedByFullName(currentUser.fullName());

        Batch rejected = repository.save(batch);
        log.info("Batch '{}' rejected", batch.getBatchNo());
        return rejected;
    }

    private BatchLogEntry latestEntry(Batch batch) {
        return batch.getEntries().stream()
                .max(Comparator.comparing(BatchLogEntry::getLineClearanceAt)
                        .thenComparing(BatchLogEntry::getCreatedAt, Comparator.nullsFirst(Comparator.naturalOrder())))
                .orElseThrow();
    }
}
