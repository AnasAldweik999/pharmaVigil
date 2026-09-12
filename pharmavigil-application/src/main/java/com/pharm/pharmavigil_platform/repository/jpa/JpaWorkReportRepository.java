package com.pharm.pharmavigil_platform.repository.jpa;

import com.pharm.pharmavigil_platform.repository.entities.WorkReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaWorkReportRepository extends JpaRepository<WorkReportEntity, UUID>, JpaSpecificationExecutor<WorkReportEntity> {

    boolean existsByStaffUserIdAndShiftIdAndReportDate(UUID staffUserId, UUID shiftId, LocalDate reportDate);

    List<WorkReportEntity> findAllByStaffUserId(UUID staffUserId);
    List<WorkReportEntity> findByStaffUserIdAndReportDate(UUID staffUserId, LocalDate reportDate);

    Optional<WorkReportEntity> findByIdAndStaffUserId(UUID id, UUID staffUserId);
}
