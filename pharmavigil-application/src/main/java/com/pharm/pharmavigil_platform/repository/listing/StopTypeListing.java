package com.pharm.pharmavigil_platform.repository.listing;

import com.pharm.pharmavigil_platform.domain.StopType;
import com.pharm.pharmavigil_platform.repository.entities.StopTypeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface StopTypeListing {
    Page<StopType> findAll(Specification<StopTypeEntity> spec, Pageable pageable);
}
