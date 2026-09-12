package com.pharm.pharmavigil_platform.service;

import com.pharm.pharmavigil_platform.repository.AllowedUnitsProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AllowedUnitsService implements AllowedUnitsProvider {

    @Value("${app.department.allowed-units}")
    private List<String> allowedUnits;

    @Override
    public List<String> getAllowedUnits() {
        return allowedUnits;
    }
}
