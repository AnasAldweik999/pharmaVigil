package com.pharm.pharmavigil_platform.service;

import com.pharm.pharmavigil_platform.domain.Batch;
import com.pharm.pharmavigil_platform.domain.BatchLogEntry;
import com.pharm.pharmavigil_platform.exception.ResourceNotFoundException;
import com.pharm.pharmavigil_platform.mapper.BatchMapper;
import com.pharm.pharmavigil_platform.repository.adapter.BatchRepositoryAdapter;
import com.pharm.pharmavigil_platform.repository.entities.BatchEntity;
import com.pharm.pharmavigil_platform.repository.specs.BatchSpec;
import com.pharm.pharmavigil_platform.repository.specs.HoldingStatusSpecFactory;
import com.pharm.pharmavigil_platform.resources.batch.BatchDetailResponse;
import com.pharm.pharmavigil_platform.resources.batch.BatchListResponse;
import com.pharm.pharmavigil_platform.resources.batch.BatchLogEntryResponse;
import com.pharm.pharmavigil_platform.resources.batch.CreateBatchLogEntryRequest;
import com.pharm.pharmavigil_platform.resources.batch.RejectBatchRequest;
import com.pharm.pharmavigil_platform.usecases.batch.CreateBatchLogEntryUseCase;
import com.pharm.pharmavigil_platform.usecases.batch.DeleteBatchLogEntryUseCase;
import com.pharm.pharmavigil_platform.usecases.batch.EnrichBatchHoldingInfoUseCase;
import com.pharm.pharmavigil_platform.usecases.batch.RejectBatchUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BatchService {

    private final BatchRepositoryAdapter adapter;
    private final CreateBatchLogEntryUseCase createBatchLogEntryUseCase;
    private final RejectBatchUseCase rejectBatchUseCase;
    private final DeleteBatchLogEntryUseCase deleteBatchLogEntryUseCase;
    private final EnrichBatchHoldingInfoUseCase enrichBatchHoldingInfoUseCase;
    private final HoldingStatusSpecFactory holdingStatusSpecFactory;
    private final BatchMapper batchMapper;

    public Page<BatchListResponse> getAll(BatchSpec spec, Pageable pageable) {
        Page<Batch> page = adapter.findAll(spec, pageable);
        page.forEach(enrichBatchHoldingInfoUseCase::execute);
        return page.map(batchMapper::toListResponse);
    }

    public Page<BatchListResponse> getAllWithHoldingStatus(BatchSpec spec, String holdingStatus, Pageable pageable) {
        Specification<BatchEntity> statusSpec = holdingStatusSpecFactory.forBatchHoldingStatus(holdingStatus);
        Specification<BatchEntity> combined = statusSpec == null ? spec
                : (spec != null ? spec.and(statusSpec) : Specification.where(statusSpec));
        Page<Batch> page = adapter.findAll(combined, pageable);
        page.forEach(enrichBatchHoldingInfoUseCase::execute);
        return page.map(batchMapper::toListResponse);
    }

    public BatchDetailResponse getByBatchNo(String batchNo) {
        Batch batch = loadChronologicallySortedBatch(batchNo);
        enrichBatchHoldingInfoUseCase.execute(batch);
        return batchMapper.toDetailResponse(batch);
    }

    @Transactional
    public BatchLogEntryResponse createLogEntry(String batchNo, CreateBatchLogEntryRequest request) {
        BatchLogEntry pendingEntry = batchMapper.toDomain(request);
        Batch batch = createBatchLogEntryUseCase.execute(batchNo, pendingEntry);
        // Identify the just-persisted row by createdAt (DB-assigned at insert time), not by
        // lineClearanceAt — that business timestamp is allowed to tie with a prior entry's, in
        // which case it can no longer tell the new row apart from an older one.
        BatchLogEntry savedEntry = batch.getEntries().stream()
                .max(Comparator.comparing(BatchLogEntry::getCreatedAt, Comparator.nullsFirst(Comparator.naturalOrder())))
                .orElseThrow();
        return batchMapper.toResponse(savedEntry);
    }

    @Transactional
    public BatchDetailResponse reject(String batchNo, RejectBatchRequest request) {
        Batch existing = adapter.findByBatchNo(batchNo)
                .orElseThrow(() -> new ResourceNotFoundException("batch.not.found"));
        Batch rejected = rejectBatchUseCase.execute(existing, request.reason(), request.lineClearanceAt());
        return batchMapper.toDetailResponse(rejected);
    }

    @Transactional
    public void deleteLogEntry(String batchNo, UUID entryId) {
        deleteBatchLogEntryUseCase.execute(batchNo, entryId);
    }

    private Batch loadChronologicallySortedBatch(String batchNo) {
        Batch batch = adapter.findByBatchNo(batchNo)
                .orElseThrow(() -> new ResourceNotFoundException("batch.not.found"));
        List<BatchLogEntry> sorted = batch.getEntries().stream()
                .sorted(Comparator.comparing(BatchLogEntry::getLineClearanceAt)
                        .thenComparing(BatchLogEntry::getCreatedAt, Comparator.nullsFirst(Comparator.naturalOrder())))
                .toList();
        batch.setEntries(sorted);
        return batch;
    }
}
