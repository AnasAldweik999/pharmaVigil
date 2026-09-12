package com.pharm.pharmavigil_platform.repository.specs;

import com.pharm.pharmavigil_platform.domain.BatchStatus;
import com.pharm.pharmavigil_platform.repository.entities.BatchEntity;
import com.pharm.pharmavigil_platform.repository.entities.DepartmentEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Builds the "alerted"/"exceeded" holding-time filters shared by the batch listing
 * (BatchService) and the departments-with-batches grid (DepartmentService), so the exclusivity
 * rule — an exceeded batch no longer counts as merely alerted — lives in exactly one place.
 * An injectable component rather than a static utility, matching this codebase's existing
 * instance-method spec pattern (e.g. DepartmentService.isActive()).
 */
@Component
public class HoldingStatusSpecFactory {

    /**
     * Returns null for a blank/omitted status (e.g. the Log Book, which has no holding-time
     * concept and must see every batch regardless of status) so callers can skip {@code .and()}
     * entirely. An explicit "all" (the Holding Time tab) still means "all currently tracked
     * batches", so — like alerted/exceeded — it excludes COMPLETED/REJECTED.
     */
    public Specification<BatchEntity> forBatchHoldingStatus(String status) {
        if (status == null || status.isBlank()) return null;
        if ("all".equalsIgnoreCase(status)) {
            return (root, query, cb) -> cb.equal(root.get("status"), BatchStatus.IN_PROGRESS);
        }
        return (root, query, cb) -> holdingStatusPredicate(cb, root, status);
    }

    /**
     * Filters departments down to those currently holding at least one matching batch
     * ({@code batch.currentDepartment == department}). Uses an EXISTS subquery rather than a
     * join — a join would duplicate a department row once per matching batch and break
     * pagination counts.
     */
    public Specification<DepartmentEntity> departmentHasBatchMatching(UUID productId, String batchNo, String holdingStatus) {
        return (root, query, cb) -> {
            Subquery<UUID> sub = query.subquery(UUID.class);
            Root<BatchEntity> batchRoot = sub.from(BatchEntity.class);
            sub.select(batchRoot.get("id"));

            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(batchRoot.get("currentDepartment"), root));
            if (productId != null) {
                predicates.add(cb.equal(batchRoot.get("product").get("id"), productId));
            }
            if (batchNo != null && !batchNo.isBlank()) {
                predicates.add(cb.like(cb.lower(batchRoot.get("batchNo")), "%" + batchNo.toLowerCase() + "%"));
            }
            if (isAll(holdingStatus)) {
                // "All" still only means all *currently tracked* batches — a department whose
                // only batches are COMPLETED/REJECTED shouldn't show up in the holding-time grid.
                predicates.add(cb.equal(batchRoot.get("status"), BatchStatus.IN_PROGRESS));
            } else {
                predicates.add(holdingStatusPredicate(cb, batchRoot, holdingStatus));
            }

            sub.where(predicates.toArray(new Predicate[0]));
            return cb.exists(sub);
        };
    }

    private Predicate holdingStatusPredicate(CriteriaBuilder cb, Path<BatchEntity> batch, String status) {
        // Alerted/exceeded only ever means anything for a batch still being tracked — the
        // scheduled job stops evaluating a batch once it's COMPLETED/REJECTED, so its flags are
        // just whatever they were frozen at and must not be surfaced here as if still current.
        Predicate running = cb.equal(batch.get("status"), BatchStatus.IN_PROGRESS);
        Predicate exceeded = cb.or(cb.isTrue(batch.get("generalHoldingExceeded")), cb.isTrue(batch.get("departmentHoldingExceeded")));
        if ("exceeded".equalsIgnoreCase(status)) return cb.and(running, exceeded);
        Predicate alerted = cb.or(cb.isTrue(batch.get("generalHoldingAlerted")), cb.isTrue(batch.get("departmentHoldingAlerted")));
        return cb.and(running, alerted, cb.not(exceeded));
    }

    private boolean isAll(String status) {
        return status == null || status.isBlank() || "all".equalsIgnoreCase(status);
    }
}
