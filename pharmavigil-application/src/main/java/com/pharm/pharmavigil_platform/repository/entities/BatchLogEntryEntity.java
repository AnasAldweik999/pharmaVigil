package com.pharm.pharmavigil_platform.repository.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "BatchLogEntry")
@Table(name = "batch_log_entries")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchLogEntryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "batch_id", nullable = false)
    private BatchEntity batch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private DepartmentEntity department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "machine_id")
    private MachineEntity machine;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Column(name = "line_clearance_at", nullable = false)
    private LocalDateTime lineClearanceAt;

    @Column(name = "is_rejected", nullable = false)
    private boolean rejected;

    @Column(name = "rejected_reason", length = 500)
    private String rejectedReason;

    @Column(name = "is_completed", nullable = false)
    private boolean completed;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "created_by", length = 255)
    private String createdBy;

    @Column(name = "created_by_full_name", length = 255)
    private String createdByFullName;
}
