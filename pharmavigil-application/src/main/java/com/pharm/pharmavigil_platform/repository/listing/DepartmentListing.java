package com.pharm.pharmavigil_platform.repository.listing;

import com.pharm.pharmavigil_platform.domain.Department;
import com.pharm.pharmavigil_platform.repository.entities.DepartmentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface DepartmentListing {
    Page<Department> findAll(Specification<DepartmentEntity> spec, Pageable pageable);
}
