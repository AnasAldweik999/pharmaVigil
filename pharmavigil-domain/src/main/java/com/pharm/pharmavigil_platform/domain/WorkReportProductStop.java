package com.pharm.pharmavigil_platform.domain;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkReportProductStop {

    private UUID id;
    private WorkReportProduct product;
    private UUID stopTypeId;
    private String stopTypeName;
    private String duration;
    private String note;
}
