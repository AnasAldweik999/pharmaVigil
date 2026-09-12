package com.pharm.pharmavigil_platform.usecases.batch;

import com.pharm.pharmavigil_platform.domain.Batch;
import com.pharm.pharmavigil_platform.domain.BatchLogEntry;
import com.pharm.pharmavigil_platform.domain.BatchLogEntryDeletion;
import com.pharm.pharmavigil_platform.domain.BatchStatus;
import com.pharm.pharmavigil_platform.repository.BatchRepository;
import com.pharm.pharmavigil_platform.validators.ValidatorChain;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class DeleteBatchLogEntryUseCase {

    private final ValidatorChain<BatchLogEntryDeletion> validators;
    private final BatchRepository repository;

    public DeleteBatchLogEntryUseCase(ValidatorChain<BatchLogEntryDeletion> validators, BatchRepository repository) {
        this.validators = validators;
        this.repository = repository;
    }

    public Optional<Batch> execute(String batchNo, UUID entryId) {
        log.info("Removing log entry '{}' from batch '{}'", entryId, batchNo);
        Batch batch = repository.findByBatchNo(batchNo).orElse(null);
        validators.validate(new BatchLogEntryDeletion(batch, entryId)).throwExceptionIfViolated();

        List<BatchLogEntry> remaining = batch.getEntries().stream()
                .filter(entry -> !entryId.equals(entry.getId()))
                .toList();

        if (remaining.isEmpty()) {
            repository.deleteById(batch.getId());
            log.info("Batch '{}' deleted after removing its last log entry", batch.getBatchNo());
            return Optional.empty();
        }

        batch.setEntries(new ArrayList<>(remaining));

        UUID previousCurrentDepartmentId = batch.getCurrentDepartmentId();

        // A completion entry carries no department/machine of its own (it's a status flag, not a
        // movement — see CreateBatchLogEntryUseCase), so when it's the latest entry, the batch's
        // location must come from the latest entry that actually has one instead, otherwise it
        // would be wiped out here. If every remaining entry is a completion (only possible after
        // deleting away every other entry), there's nothing to derive location from — leave the
        // batch's existing location untouched rather than nulling it.
        BatchLogEntry latestLocationEntry = remaining.stream()
                .filter(e -> !e.isCompleted())
                .max(Comparator.comparing(BatchLogEntry::getLineClearanceAt)
                        .thenComparing(BatchLogEntry::getCreatedAt, Comparator.nullsFirst(Comparator.naturalOrder())))
                .orElse(null);

        if (latestLocationEntry != null) {
            batch.setCurrentDepartmentId(latestLocationEntry.getDepartmentId());
            batch.setCurrentDepartmentName(latestLocationEntry.getDepartmentName());
            batch.setCurrentMachineId(latestLocationEntry.getMachineId());
            batch.setCurrentMachineName(latestLocationEntry.getMachineName());

            // Recompute the current-department holding-time window: walk backward from the latest
            // location-bearing entry while the department stays the same (skipping completion
            // entries along the way), mirroring how `firstLoggedAt` below is recomputed from the
            // earliest surviving entry.
            List<BatchLogEntry> chronological = remaining.stream()
                    .sorted(Comparator.comparing(BatchLogEntry::getLineClearanceAt)
                            .thenComparing(BatchLogEntry::getCreatedAt, Comparator.nullsFirst(Comparator.naturalOrder())))
                    .toList();
            LocalDateTime enteredAt = null;
            for (int i = chronological.size() - 1; i >= 0; i--) {
                BatchLogEntry e = chronological.get(i);
                if (e.isCompleted()) continue;
                if (!Objects.equals(e.getDepartmentId(), latestLocationEntry.getDepartmentId())) break;
                enteredAt = e.getLineClearanceAt();
            }
            batch.setCurrentDepartmentEnteredAt(enteredAt);
            if (!Objects.equals(previousCurrentDepartmentId, latestLocationEntry.getDepartmentId())) {
                // Deleting the most recent entry can revert the batch to an earlier department —
                // don't carry over stale alert/exceed flags from the department it just reverted out of.
                batch.setDepartmentHoldingAlerted(false);
                batch.setDepartmentHoldingExceeded(false);
            }
        }

        BatchLogEntry earliest = remaining.stream()
                .min(Comparator.comparing(BatchLogEntry::getLineClearanceAt)
                        .thenComparing(BatchLogEntry::getCreatedAt, Comparator.nullsFirst(Comparator.naturalOrder())))
                .orElseThrow();
        batch.setFirstLoggedAt(earliest.getLineClearanceAt());

        if (remaining.stream().anyMatch(BatchLogEntry::isRejected)) {
            batch.setStatus(BatchStatus.REJECTED);
        } else if (remaining.stream().anyMatch(BatchLogEntry::isCompleted)) {
            batch.setStatus(BatchStatus.COMPLETED);
        } else {
            batch.setStatus(BatchStatus.IN_PROGRESS);
        }

        BatchLogEntry mostRecentlyEntered = remaining.stream()
                .max(Comparator.comparing(BatchLogEntry::getCreatedAt, Comparator.nullsFirst(Comparator.naturalOrder())))
                .orElseThrow();
        batch.setLastUpdatedAt(mostRecentlyEntered.getCreatedAt());
        batch.setLastUpdatedBy(mostRecentlyEntered.getCreatedBy());
        batch.setLastUpdatedByFullName(mostRecentlyEntered.getCreatedByFullName());

        Batch saved = repository.save(batch);
        log.info("Log entry '{}' removed from batch '{}'", entryId, batchNo);
        return Optional.of(saved);
    }
}
