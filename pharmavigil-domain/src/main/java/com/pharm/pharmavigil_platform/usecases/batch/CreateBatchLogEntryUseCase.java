package com.pharm.pharmavigil_platform.usecases.batch;

import com.pharm.pharmavigil_platform.domain.Batch;
import com.pharm.pharmavigil_platform.domain.BatchLogEntry;
import com.pharm.pharmavigil_platform.domain.BatchStatus;
import com.pharm.pharmavigil_platform.repository.BatchRepository;
import com.pharm.pharmavigil_platform.security.IdentityProvider;
import com.pharm.pharmavigil_platform.validators.ValidatorChain;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Objects;

@Slf4j
public class CreateBatchLogEntryUseCase {

    private final ValidatorChain<Batch> validators;
    private final EnrichBatchLogEntryUseCase enrichBatchLogEntryUseCase;
    private final BatchRepository repository;
    private final IdentityProvider identityProvider;

    public CreateBatchLogEntryUseCase(ValidatorChain<Batch> validators,
                                       EnrichBatchLogEntryUseCase enrichBatchLogEntryUseCase,
                                       BatchRepository repository,
                                       IdentityProvider identityProvider) {
        this.validators = validators;
        this.enrichBatchLogEntryUseCase = enrichBatchLogEntryUseCase;
        this.repository = repository;
        this.identityProvider = identityProvider;
    }

    public Batch execute(String batchNo, BatchLogEntry pendingEntry) {
        log.info("Logging batch entry for batch '{}'", batchNo);
        Batch target = repository.findByBatchNo(batchNo).orElseGet(() -> Batch.builder()
                .batchNo(batchNo)
                .productId(pendingEntry.getProductId())
                .status(BatchStatus.IN_PROGRESS)
                .entries(new ArrayList<>())
                .build());

        if (pendingEntry.isCompleted()) {
            pendingEntry.setDepartmentId(null);
            pendingEntry.setMachineId(null);
        }

        target.getEntries().add(pendingEntry);

        validators.validate(target).throwExceptionIfViolated();
        enrichBatchLogEntryUseCase.execute(pendingEntry);

        var currentUser = identityProvider.getCurrentUser();
        pendingEntry.setCreatedBy(currentUser.username());
        pendingEntry.setCreatedByFullName(currentUser.fullName());
        if (target.getId() == null) {
            target.setCreatedBy(currentUser.username());
            target.setCreatedByFullName(currentUser.fullName());
            target.setFirstLoggedAt(pendingEntry.getLineClearanceAt());
        }
        target.setLastUpdatedAt(Instant.now());
        target.setLastUpdatedBy(currentUser.username());
        target.setLastUpdatedByFullName(currentUser.fullName());

        if (!pendingEntry.isCompleted()) {
            // The entry just submitted is always the new "current" state — not whichever entry has the
            // max lineClearanceAt, since ties are allowed (same date/time as the last log is valid) and
            // a tie-break search would non-deterministically favor an older entry over this one.
            boolean departmentChanged = pendingEntry.getDepartmentId() != null
                    && !Objects.equals(target.getCurrentDepartmentId(), pendingEntry.getDepartmentId());
            target.setCurrentDepartmentId(pendingEntry.getDepartmentId());
            target.setCurrentDepartmentName(pendingEntry.getDepartmentName());
            if (departmentChanged) {
                // A genuinely new department starts a fresh holding-time window — a terminal
                // department's repeat-visit-with-different-machine keeps the same departmentId, so
                // it never trips this branch and the window continues uninterrupted.
                target.setCurrentDepartmentEnteredAt(pendingEntry.getLineClearanceAt());
                target.setDepartmentHoldingAlerted(false);
                target.setDepartmentHoldingExceeded(false);
            }
            target.setCurrentMachineId(pendingEntry.getMachineId());
            target.setCurrentMachineName(pendingEntry.getMachineName());
        }
        // Completion is a terminal status flag, not a physical movement — the batch's last known
        // department/machine (and holding-time window) is left exactly as it was, the same way
        // RejectBatchUseCase already leaves location untouched for rejections. This is what lets
        // completed/rejected batches keep showing up in department-scoped batch listings instead
        // of vanishing once finalized.
        if (pendingEntry.isCompleted()) {
            target.setStatus(BatchStatus.COMPLETED);
        } else {
            target.setStatus(BatchStatus.IN_PROGRESS);
        }

        Batch saved = repository.save(target);
        log.info("Batch log entry saved for batch '{}'", batchNo);
        return saved;
    }
}
