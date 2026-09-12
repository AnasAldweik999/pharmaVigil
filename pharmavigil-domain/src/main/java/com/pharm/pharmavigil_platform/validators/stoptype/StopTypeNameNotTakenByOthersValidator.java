package com.pharm.pharmavigil_platform.validators.stoptype;

import com.pharm.pharmavigil_platform.domain.StopType;
import com.pharm.pharmavigil_platform.repository.StopTypeRepository;
import com.pharm.pharmavigil_platform.validators.Validator;
import com.pharm.pharmavigil_platform.validators.models.SystemViolation;

import java.util.LinkedHashSet;
import java.util.Set;

public class StopTypeNameNotTakenByOthersValidator implements Validator<StopType> {

    private final StopTypeRepository repository;

    public StopTypeNameNotTakenByOthersValidator(StopTypeRepository repository) {
        this.repository = repository;
    }

    @Override
    public Set<SystemViolation> validate(StopType stopType) {
        Set<SystemViolation> violations = new LinkedHashSet<>();
        if (repository.existsByNameIgnoreCaseAndIdNot(stopType.getName(), stopType.getId())) {
            violations.add(new SystemViolation("name", "stop.type.name.already.exists"));
        }
        return violations;
    }
}
