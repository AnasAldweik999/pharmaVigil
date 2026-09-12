package com.pharm.pharmavigil_platform.repository.entities;

import com.pharm.pharmavigil_platform.domain.BatchStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity(name = "Batch")
@Table(name = "batches", uniqueConstraints = @UniqueConstraint(name = "uq_batch_no", columnNames = "batch_no"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "batch_no", nullable = false, unique = true, length = 100)
    private String batchNo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private BatchStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_department_id")
    private DepartmentEntity currentDepartment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_machine_id")
    private MachineEntity currentMachine;

    @Column(name = "first_logged_at")
    private LocalDateTime firstLoggedAt;

    @Column(name = "current_department_entered_at")
    private LocalDateTime currentDepartmentEnteredAt;

    @Column(name = "general_holding_alerted", nullable = false)
    private boolean generalHoldingAlerted;

    @Column(name = "general_holding_exceeded", nullable = false)
    private boolean generalHoldingExceeded;

    @Column(name = "department_holding_alerted", nullable = false)
    private boolean departmentHoldingAlerted;

    @Column(name = "department_holding_exceeded", nullable = false)
    private boolean departmentHoldingExceeded;

    @Builder.Default
    @OneToMany(mappedBy = "batch", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BatchLogEntryEntity> entries = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "created_by", length = 255)
    private String createdBy;

    @Column(name = "created_by_full_name", length = 255)
    private String createdByFullName;

    @Column(name = "last_updated_at")
    private Instant lastUpdatedAt;

    @Column(name = "last_updated_by", length = 255)
    private String lastUpdatedBy;

    @Column(name = "last_updated_by_full_name", length = 255)
    private String lastUpdatedByFullName;
}
