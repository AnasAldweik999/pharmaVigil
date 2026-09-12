package com.pharm.pharmavigil_platform.controller.staff;

import com.pharm.pharmavigil_platform.domain.UserRole;
import com.pharm.pharmavigil_platform.repository.specs.BatchSpec;
import com.pharm.pharmavigil_platform.resources.batch.BatchDetailResponse;
import com.pharm.pharmavigil_platform.resources.batch.BatchListResponse;
import com.pharm.pharmavigil_platform.resources.batch.BatchLogEntryResponse;
import com.pharm.pharmavigil_platform.resources.batch.CreateBatchLogEntryRequest;
import com.pharm.pharmavigil_platform.resources.batch.RejectBatchRequest;
import com.pharm.pharmavigil_platform.security.RequiresRole;
import com.pharm.pharmavigil_platform.service.BatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/staff/batches")
@RequiredArgsConstructor
public class BatchController {

    private final BatchService batchService;

    @GetMapping
    @RequiresRole(UserRole.BATCH_LOG_REPORTER)
    public ResponseEntity<Page<BatchListResponse>> getAll(BatchSpec spec, Pageable pageable) {
        return ResponseEntity.ok(batchService.getAll(spec, pageable));
    }

    @PostMapping("/{batchNo}/entries")
    @RequiresRole(UserRole.BATCH_LOG_REPORTER)
    public ResponseEntity<BatchLogEntryResponse> createEntry(@PathVariable String batchNo,
                                                              @RequestBody CreateBatchLogEntryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(batchService.createLogEntry(batchNo, request));
    }

    @PostMapping("/{batchNo}/reject")
    @RequiresRole(UserRole.BATCH_LOG_REPORTER)
    public ResponseEntity<BatchDetailResponse> reject(@PathVariable String batchNo,
                                                       @RequestBody RejectBatchRequest request) {
        return ResponseEntity.ok(batchService.reject(batchNo, request));
    }

    @GetMapping("/{batchNo}")
    @RequiresRole(UserRole.BATCH_LOG_REPORTER)
    public ResponseEntity<BatchDetailResponse> getByBatchNo(@PathVariable String batchNo) {
        return ResponseEntity.ok(batchService.getByBatchNo(batchNo));
    }
}
