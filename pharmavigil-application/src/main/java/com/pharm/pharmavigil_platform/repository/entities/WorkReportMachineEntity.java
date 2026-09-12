package com.pharm.pharmavigil_platform.repository.entities;

import com.pharm.pharmavigil_platform.domain.MachineStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity(name = "WorkReportMachine")
@Table(name = "work_report_machines")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkReportMachineEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "work_report_id", nullable = false)
    private WorkReportEntity workReport;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "machine_id", nullable = false)
    private MachineEntity machine;

    @Column(name = "machine_name", nullable = false, length = 100)
    private String machineName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private DepartmentEntity department;

    @Column(name = "department_name", length = 100)
    private String departmentName;

    @Enumerated(EnumType.STRING)
    @Column(name = "machine_status", nullable = false, length = 20)
    private MachineStatus status;

    @Builder.Default
    @OneToMany(mappedBy = "workReportMachine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkReportProductEntity> products = new ArrayList<>();
}
