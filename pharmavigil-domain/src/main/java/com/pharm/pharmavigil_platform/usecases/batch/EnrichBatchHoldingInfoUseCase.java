package com.pharm.pharmavigil_platform.usecases.batch;

import com.pharm.pharmavigil_platform.domain.Batch;
import com.pharm.pharmavigil_platform.domain.Department;
import com.pharm.pharmavigil_platform.domain.EffectiveHoldingTime;
import com.pharm.pharmavigil_platform.domain.ExceptionalHolding;
import com.pharm.pharmavigil_platform.repository.DepartmentRepository;

public class EnrichBatchHoldingInfoUseCase {

    private final DepartmentRepository departmentRepository;
    private final EffectiveHoldingTime effectiveHoldingTime;

    public EnrichBatchHoldingInfoUseCase(DepartmentRepository departmentRepository, EffectiveHoldingTime effectiveHoldingTime) {
        this.departmentRepository = departmentRepository;
        this.effectiveHoldingTime = effectiveHoldingTime;
    }

    public void execute(Batch batch) {
        if (batch.getCurrentDepartmentId() == null) {
            batch.setEffectiveHoldingTimeDays(0);
            batch.setUsingExceptionalHoldingTime(false);
            return;
        }

        Department department = departmentRepository.findById(batch.getCurrentDepartmentId()).orElse(null);
        if (department == null) {
            batch.setEffectiveHoldingTimeDays(0);
            batch.setUsingExceptionalHoldingTime(false);
            return;
        }

        ExceptionalHolding exceptional = effectiveHoldingTime.findExceptional(department, batch.getProductId());
        batch.setEffectiveHoldingTimeDays(exceptional != null ? exceptional.getHoldingTimeDays() : department.getStandardHoldingTime());
        batch.setUsingExceptionalHoldingTime(exceptional != null);
    }
}
