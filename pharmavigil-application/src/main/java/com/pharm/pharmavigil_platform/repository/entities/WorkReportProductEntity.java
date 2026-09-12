package com.pharm.pharmavigil_platform.repository.entities;

import com.pharm.pharmavigil_platform.repository.converters.StageCompletionMapConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Formula;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity(name = "WorkReportProduct")
@Table(name = "work_report_products")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkReportProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "work_report_machine_id", nullable = false)
    private WorkReportMachineEntity workReportMachine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;

    @Column(name = "batch_no", nullable = false, length = 100)
    private String batchNo;

    @Column(name = "output_units", nullable = false)
    private long output;

    @Column(name = "unit", length = 100)
    private String unit;

    @Column(name = "consignee", length = 255)
    private String consignee;

    @Column(nullable = false)
    @Builder.Default
    private boolean deviation = false;

    @Column(name = "deviation_details", length = 500)
    private String deviationDetails;

    @Column(nullable = false)
    @Builder.Default
    private boolean hold = false;

    @Column(name = "hold_details", length = 500)
    private String holdDetails;

    @Convert(converter = StageCompletionMapConverter.class)
    @Column(name = "stages", columnDefinition = "text")
    private Map<String, Boolean> stages;

    @Builder.Default
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkReportProductStopEntity> stops = new ArrayList<>();

    @Formula("(SELECT COUNT(s.id) FROM work_report_product_stops s WHERE s.work_report_product_id = id)")
    private int stopCount;
}
