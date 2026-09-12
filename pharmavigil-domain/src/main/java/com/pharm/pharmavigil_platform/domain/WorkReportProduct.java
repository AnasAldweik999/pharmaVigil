package com.pharm.pharmavigil_platform.domain;

import lombok.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkReportProduct {

    private UUID id;
    private WorkReportMachine workReportMachine;
    private UUID productId;
    private String productName;
    private String batchNo;
    private long output;
    private String unit;
    private String consignee;
    @Builder.Default
    private boolean deviation = false;
    private String deviationDetails;
    @Builder.Default
    private boolean hold = false;
    private String holdDetails;
    @Builder.Default
    private Map<String, Boolean> stages = new LinkedHashMap<>();
    @Builder.Default
    private List<WorkReportProductStop> stops = new ArrayList<>();
    private int stopCount;
}
