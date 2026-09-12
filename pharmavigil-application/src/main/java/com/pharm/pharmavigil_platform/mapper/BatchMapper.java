package com.pharm.pharmavigil_platform.mapper;

import com.pharm.pharmavigil_platform.domain.Batch;
import com.pharm.pharmavigil_platform.domain.BatchLogEntry;
import com.pharm.pharmavigil_platform.domain.BatchStatus;
import com.pharm.pharmavigil_platform.repository.entities.BatchEntity;
import com.pharm.pharmavigil_platform.repository.entities.BatchLogEntryEntity;
import com.pharm.pharmavigil_platform.resources.batch.BatchDetailResponse;
import com.pharm.pharmavigil_platform.resources.batch.BatchListResponse;
import com.pharm.pharmavigil_platform.resources.batch.BatchLogEntryResponse;
import com.pharm.pharmavigil_platform.resources.batch.CreateBatchLogEntryRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Objects;

@Mapper(componentModel = "spring")
public interface BatchMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "departmentName", ignore = true)
    @Mapping(target = "terminalDepartment", ignore = true)
    @Mapping(target = "machineName", ignore = true)
    @Mapping(target = "productName", ignore = true)
    @Mapping(target = "rejected", ignore = true)
    @Mapping(target = "rejectedReason", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    BatchLogEntry toDomain(CreateBatchLogEntryRequest request);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "currentDepartmentId", source = "currentDepartment.id")
    @Mapping(target = "currentDepartmentName", source = "currentDepartment.name")
    @Mapping(target = "currentMachineId", source = "currentMachine.id")
    @Mapping(target = "currentMachineName", source = "currentMachine.name")
    @Mapping(target = "effectiveHoldingTimeDays", ignore = true)
    @Mapping(target = "usingExceptionalHoldingTime", ignore = true)
    Batch toDomain(BatchEntity entity);

    // Used only by the scheduled holding-time job's batch fetch — deliberately ignores the lazy
    // `entries` collection so it never gets touched outside a request-scoped Hibernate session.
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "currentDepartmentId", source = "currentDepartment.id")
    @Mapping(target = "currentDepartmentName", source = "currentDepartment.name")
    @Mapping(target = "currentMachineId", source = "currentMachine.id")
    @Mapping(target = "currentMachineName", source = "currentMachine.name")
    @Mapping(target = "effectiveHoldingTimeDays", ignore = true)
    @Mapping(target = "usingExceptionalHoldingTime", ignore = true)
    @Mapping(target = "entries", ignore = true)
    Batch toDomainSummary(BatchEntity entity);

    @Mapping(target = "departmentId", source = "department.id")
    @Mapping(target = "departmentName", source = "department.name")
    @Mapping(target = "terminalDepartment", source = "department.terminalDepartment")
    @Mapping(target = "machineId", source = "machine.id")
    @Mapping(target = "machineName", source = "machine.name")
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    BatchLogEntry toDomain(BatchLogEntryEntity entity);

    @Mapping(target = "isRejected", source = "rejected")
    @Mapping(target = "isCompleted", source = "completed")
    BatchLogEntryResponse toResponse(BatchLogEntry entry);

    @Mapping(target = "daysSinceFirstLog", expression = "java(java.time.temporal.ChronoUnit.DAYS.between(batch.getFirstLoggedAt().toLocalDate(), asOfDate(batch)))")
    @Mapping(target = "daysInCurrentDepartment", expression = "java(batch.getCurrentDepartmentEnteredAt() != null ? java.time.temporal.ChronoUnit.DAYS.between(batch.getCurrentDepartmentEnteredAt().toLocalDate(), asOfDate(batch)) : 0L)")
    @Mapping(target = "effectiveDepartmentHoldingTimeDays", source = "effectiveHoldingTimeDays")
    BatchListResponse toListResponse(Batch batch);

    @Mapping(target = "daysSinceFirstLog", expression = "java(java.time.temporal.ChronoUnit.DAYS.between(batch.getFirstLoggedAt().toLocalDate(), asOfDate(batch)))")
    @Mapping(target = "daysInCurrentDepartment", expression = "java(batch.getCurrentDepartmentEnteredAt() != null ? java.time.temporal.ChronoUnit.DAYS.between(batch.getCurrentDepartmentEnteredAt().toLocalDate(), asOfDate(batch)) : 0L)")
    @Mapping(target = "effectiveDepartmentHoldingTimeDays", source = "effectiveHoldingTimeDays")
    BatchDetailResponse toDetailResponse(Batch batch);

    /**
     * The date to measure day-counts against: for a finished batch (COMPLETED/REJECTED), that's
     * frozen at the moment it finished — its completion/rejection entry's lineClearanceAt —
     * rather than today, so daysSinceFirstLog/daysInCurrentDepartment stop climbing once a batch
     * is no longer being tracked (matches the scheduled job, which also stops evaluating it then).
     * For a still-running batch, it's simply today.
     */
    default LocalDate asOfDate(Batch batch) {
        if (batch.getStatus() != BatchStatus.COMPLETED && batch.getStatus() != BatchStatus.REJECTED) {
            return LocalDate.now();
        }
        return batch.getEntries().stream()
                .filter(entry -> entry.isCompleted() || entry.isRejected())
                .map(BatchLogEntry::getLineClearanceAt)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .map(LocalDateTime::toLocalDate)
                .orElseGet(LocalDate::now);
    }
}
