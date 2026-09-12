package com.pharm.pharmavigil_platform.repository.jpa;

import com.pharm.pharmavigil_platform.domain.BatchStatus;
import com.pharm.pharmavigil_platform.repository.entities.BatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaBatchRepository extends JpaRepository<BatchEntity, UUID>, JpaSpecificationExecutor<BatchEntity> {

    Optional<BatchEntity> findByBatchNo(String batchNo);

    List<BatchEntity> findByStatusIn(List<BatchStatus> statuses);

    @Modifying
    @Query("UPDATE Batch b SET b.generalHoldingAlerted = :generalAlerted, b.generalHoldingExceeded = :generalExceeded, "
            + "b.departmentHoldingAlerted = :departmentAlerted, b.departmentHoldingExceeded = :departmentExceeded WHERE b.id = :batchId")
    void updateHoldingFlags(@Param("batchId") UUID batchId,
                             @Param("generalAlerted") boolean generalAlerted,
                             @Param("generalExceeded") boolean generalExceeded,
                             @Param("departmentAlerted") boolean departmentAlerted,
                             @Param("departmentExceeded") boolean departmentExceeded);
}
