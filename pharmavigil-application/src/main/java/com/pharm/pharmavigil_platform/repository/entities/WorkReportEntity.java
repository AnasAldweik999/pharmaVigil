package com.pharm.pharmavigil_platform.repository.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity(name = "WorkReport")
@Table(name = "work_reports",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_work_report_staff_shift_date",
                columnNames = {"staff_user_id", "shift_id", "report_date"}
        ))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkReportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "staff_user_id", nullable = false)
    private UserEntity staffUser;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shift_id", nullable = false)
    private ShiftEntity shift;

    @Column(name = "shift_name", nullable = false, length = 100)
    private String shiftName;

    @Column(name = "report_date", nullable = false)
    private LocalDate reportDate;

    @Builder.Default
    @OneToMany(mappedBy = "workReport", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkReportMachineEntity> machines = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
