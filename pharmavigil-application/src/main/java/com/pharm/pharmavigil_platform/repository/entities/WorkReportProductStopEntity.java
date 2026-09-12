package com.pharm.pharmavigil_platform.repository.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity(name = "WorkReportProductStop")
@Table(name = "work_report_product_stops")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkReportProductStopEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "work_report_product_id", nullable = false)
    private WorkReportProductEntity product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stop_type_id")
    private StopTypeEntity stopType;

    @Column(name = "stop_type_name", nullable = false, length = 100)
    private String stopTypeName;

    @Column(name = "duration", nullable = false)
    private long duration;

    @Column(name = "note", length = 255)
    private String note;
}
