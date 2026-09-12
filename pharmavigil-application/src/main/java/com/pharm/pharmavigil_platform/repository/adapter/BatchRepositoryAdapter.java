package com.pharm.pharmavigil_platform.repository.adapter;

import com.pharm.pharmavigil_platform.domain.Batch;
import com.pharm.pharmavigil_platform.domain.BatchLogEntry;
import com.pharm.pharmavigil_platform.mapper.BatchMapper;
import com.pharm.pharmavigil_platform.repository.BatchRepository;
import com.pharm.pharmavigil_platform.repository.entities.BatchEntity;
import com.pharm.pharmavigil_platform.repository.entities.BatchLogEntryEntity;
import com.pharm.pharmavigil_platform.repository.jpa.JpaBatchRepository;
import com.pharm.pharmavigil_platform.repository.jpa.JpaDepartmentRepository;
import com.pharm.pharmavigil_platform.repository.jpa.JpaMachineRepository;
import com.pharm.pharmavigil_platform.repository.jpa.JpaProductRepository;
import com.pharm.pharmavigil_platform.repository.listing.BatchListing;
import com.pharm.pharmavigil_platform.domain.BatchStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BatchRepositoryAdapter implements BatchRepository, BatchListing {

    private final JpaBatchRepository jpaBatchRepository;
    private final JpaDepartmentRepository jpaDepartmentRepository;
    private final JpaMachineRepository jpaMachineRepository;
    private final JpaProductRepository jpaProductRepository;
    private final BatchMapper batchMapper;

    @Override
    public Optional<Batch> findByBatchNo(String batchNo) {
        return jpaBatchRepository.findByBatchNo(batchNo).map(batchMapper::toDomain);
    }

    @Override
    @Transactional
    public Batch save(Batch batch) {
        BatchEntity entity;
        Map<UUID, BatchLogEntryEntity> existingById;

        if (batch.getId() != null) {
            entity = jpaBatchRepository.findById(batch.getId()).orElseThrow();
            existingById = entity.getEntries().stream()
                    .collect(Collectors.toMap(BatchLogEntryEntity::getId, e -> e));
        } else {
            entity = new BatchEntity();
            entity.setEntries(new ArrayList<>());
            existingById = Map.of();
        }

        entity.setBatchNo(batch.getBatchNo());
        entity.setProduct(jpaProductRepository.getReferenceById(batch.getProductId()));
        entity.setStatus(batch.getStatus());
        entity.setCurrentDepartment(batch.getCurrentDepartmentId() != null
                ? jpaDepartmentRepository.getReferenceById(batch.getCurrentDepartmentId()) : null);
        entity.setCurrentMachine(batch.getCurrentMachineId() != null
                ? jpaMachineRepository.getReferenceById(batch.getCurrentMachineId()) : null);
        entity.setFirstLoggedAt(batch.getFirstLoggedAt());
        entity.setCurrentDepartmentEnteredAt(batch.getCurrentDepartmentEnteredAt());
        entity.setGeneralHoldingAlerted(batch.isGeneralHoldingAlerted());
        entity.setGeneralHoldingExceeded(batch.isGeneralHoldingExceeded());
        entity.setDepartmentHoldingAlerted(batch.isDepartmentHoldingAlerted());
        entity.setDepartmentHoldingExceeded(batch.isDepartmentHoldingExceeded());
        entity.setCreatedBy(batch.getCreatedBy());
        entity.setCreatedByFullName(batch.getCreatedByFullName());
        entity.setLastUpdatedAt(batch.getLastUpdatedAt());
        entity.setLastUpdatedBy(batch.getLastUpdatedBy());
        entity.setLastUpdatedByFullName(batch.getLastUpdatedByFullName());

        // Rebuild the entries collection: entries already persisted are kept as-is (immutable —
        // rejecting a batch appends a new entry rather than mutating an existing one), new entries
        // (id == null) are created, and any row missing from this rebuilt list is removed
        // automatically by orphanRemoval — this is how both "append" and "delete one entry" are
        // handled through the same save() call, mirroring DepartmentRepositoryAdapter's
        // exceptionalHoldings rebuild.
        List<BatchLogEntryEntity> rebuilt = new ArrayList<>();
        for (BatchLogEntry domainEntry : batch.getEntries()) {
            BatchLogEntryEntity entryEntity = domainEntry.getId() != null ? existingById.get(domainEntry.getId()) : null;
            if (entryEntity == null) {
                entryEntity = new BatchLogEntryEntity();
                entryEntity.setBatch(entity);
                entryEntity.setDepartment(domainEntry.getDepartmentId() != null
                        ? jpaDepartmentRepository.getReferenceById(domainEntry.getDepartmentId()) : null);
                entryEntity.setMachine(domainEntry.getMachineId() != null
                        ? jpaMachineRepository.getReferenceById(domainEntry.getMachineId()) : null);
                entryEntity.setProduct(jpaProductRepository.getReferenceById(domainEntry.getProductId()));
                entryEntity.setLineClearanceAt(domainEntry.getLineClearanceAt());
                entryEntity.setRejected(domainEntry.isRejected());
                entryEntity.setRejectedReason(domainEntry.getRejectedReason());
                entryEntity.setCompleted(domainEntry.isCompleted());
                entryEntity.setCreatedBy(domainEntry.getCreatedBy());
                entryEntity.setCreatedByFullName(domainEntry.getCreatedByFullName());
            }
            rebuilt.add(entryEntity);
        }
        entity.getEntries().clear();
        entity.getEntries().addAll(rebuilt);

        BatchEntity saved = jpaBatchRepository.save(entity);
        return batchMapper.toDomain(jpaBatchRepository.findById(saved.getId()).orElseThrow());
    }

    @Override
    public void deleteById(UUID id) {
        jpaBatchRepository.deleteById(id);
    }

    @Override
    public List<Batch> findAllByStatusIn(List<BatchStatus> statuses) {
        return jpaBatchRepository.findByStatusIn(statuses).stream().map(batchMapper::toDomainSummary).toList();
    }

    @Override
    @Transactional
    public void updateHoldingFlags(UUID batchId, boolean generalAlerted, boolean generalExceeded,
                                    boolean departmentAlerted, boolean departmentExceeded) {
        jpaBatchRepository.updateHoldingFlags(batchId, generalAlerted, generalExceeded, departmentAlerted, departmentExceeded);
    }

    @Override
    public Page<Batch> findAll(Specification<BatchEntity> spec, Pageable pageable) {
        return jpaBatchRepository.findAll(spec, pageable).map(batchMapper::toDomain);
    }
}
