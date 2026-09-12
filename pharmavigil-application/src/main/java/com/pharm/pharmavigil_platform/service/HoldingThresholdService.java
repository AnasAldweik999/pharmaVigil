package com.pharm.pharmavigil_platform.service;

import com.pharm.pharmavigil_platform.repository.GeneralHoldingThresholdProvider;
import com.pharm.pharmavigil_platform.repository.HoldingAlertEmailProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class HoldingThresholdService implements GeneralHoldingThresholdProvider, HoldingAlertEmailProvider {

    @Value("${app.holding.general-threshold-days:45}")
    private int generalHoldingThresholdDays;

    @Value("${app.holding.alert-email.enabled:false}")
    private boolean holdingAlertEmailEnabled;

    @Override
    public int getGeneralHoldingThresholdDays() {
        return generalHoldingThresholdDays;
    }

    @Override
    public boolean isHoldingAlertEmailEnabled() {
        return holdingAlertEmailEnabled;
    }
}
