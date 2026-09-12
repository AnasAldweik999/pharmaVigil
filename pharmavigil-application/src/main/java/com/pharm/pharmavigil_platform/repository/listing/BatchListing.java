package com.pharm.pharmavigil_platform.repository.listing;

import com.pharm.pharmavigil_platform.domain.Batch;
import com.pharm.pharmavigil_platform.repository.entities.BatchEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface BatchListing {
    Page<Batch> findAll(Specification<BatchEntity> spec, Pageable pageable);
}
