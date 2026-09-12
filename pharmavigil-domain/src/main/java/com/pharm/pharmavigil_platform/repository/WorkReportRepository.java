package com.pharm.pharmavigil_platform.repository;

import com.pharm.pharmavigil_platform.domain.WorkReport;

import java.time.LocalDate;
import java.util.UUID;

public interface WorkReportRepository {
    boolean existsById(UUID id);
    void deleteById(UUID id);
    WorkReport save(WorkReport workReport);
    boolean existsByStaffUserIdAndShiftIdAndReportDate(UUID staffUserId, UUID shiftId, LocalDate reportDate);
}
