package com.pharm.pharmavigil_platform.repository;

import com.pharm.pharmavigil_platform.domain.Batch;
import com.pharm.pharmavigil_platform.domain.BatchStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BatchRepository {
    Optional<Batch> findByBatchNo(String batchNo);
    Batch save(Batch batch);
    void deleteById(UUID id);
    List<Batch> findAllByStatusIn(List<BatchStatus> statuses);
    void updateHoldingFlags(UUID batchId, boolean generalAlerted, boolean generalExceeded,
                             boolean departmentAlerted, boolean departmentExceeded);
}
