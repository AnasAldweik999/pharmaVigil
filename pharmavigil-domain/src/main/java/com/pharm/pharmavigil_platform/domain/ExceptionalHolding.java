package com.pharm.pharmavigil_platform.domain;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionalHolding {

    private UUID id;
    private UUID productId;
    private int holdingTimeDays;
}
