package com.pharm.pharmavigil_platform.repository.jpa;

import com.pharm.pharmavigil_platform.repository.entities.WorkReportProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface JpaWorkReportProductRepository
        extends JpaRepository<WorkReportProductEntity, UUID>, JpaSpecificationExecutor<WorkReportProductEntity> {}
