package com.pharm.pharmavigil_platform.repository.listing;

import com.pharm.pharmavigil_platform.repository.entities.WorkReportEntity;
import com.pharm.pharmavigil_platform.repository.specs.SupervisorWorkReportFilterSpec;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WorkReportListing {
    Page<WorkReportEntity> findAll(SupervisorWorkReportFilterSpec spec, Pageable pageable);
}
