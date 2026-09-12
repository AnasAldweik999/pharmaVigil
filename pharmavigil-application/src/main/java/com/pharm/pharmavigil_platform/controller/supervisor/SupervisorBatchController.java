package com.pharm.pharmavigil_platform.controller.supervisor;

import com.pharm.pharmavigil_platform.domain.UserRole;
import com.pharm.pharmavigil_platform.repository.specs.BatchSpec;
import com.pharm.pharmavigil_platform.resources.batch.BatchDetailResponse;
import com.pharm.pharmavigil_platform.resources.batch.BatchListResponse;
import com.pharm.pharmavigil_platform.security.RequiresRole;
import com.pharm.pharmavigil_platform.service.BatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/supervisor/batches")
@RequiredArgsConstructor
public class SupervisorBatchController {

    private final BatchService batchService;

    @GetMapping
    @RequiresRole(UserRole.BATCH_TRACKING_MANAGER)
    public ResponseEntity<Page<BatchListResponse>> getAll(BatchSpec spec,
            @RequestParam(required = false) String holdingStatus,
            Pageable pageable) {
        return ResponseEntity.ok(batchService.getAllWithHoldingStatus(spec, holdingStatus, pageable));
    }

    @GetMapping("/{batchNo}")
    @RequiresRole(UserRole.BATCH_TRACKING_MANAGER)
    public ResponseEntity<BatchDetailResponse> getByBatchNo(@PathVariable String batchNo) {
        return ResponseEntity.ok(batchService.getByBatchNo(batchNo));
    }

    @DeleteMapping("/{batchNo}/entries/{entryId}")
    @RequiresRole(UserRole.BATCH_TRACKING_MANAGER)
    public ResponseEntity<Void> deleteEntry(@PathVariable String batchNo, @PathVariable UUID entryId) {
        batchService.deleteLogEntry(batchNo, entryId);
        return ResponseEntity.noContent().build();
    }
}
