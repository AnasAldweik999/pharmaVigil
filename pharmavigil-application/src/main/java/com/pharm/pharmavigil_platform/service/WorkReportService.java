package com.pharm.pharmavigil_platform.service;

import com.pharm.pharmavigil_platform.exception.ResourceNotFoundException;
import com.pharm.pharmavigil_platform.mapper.WorkReportMapper;
import com.pharm.pharmavigil_platform.repository.entities.WorkReportEntity;
import com.pharm.pharmavigil_platform.repository.jpa.JpaWorkReportRepository;
import com.pharm.pharmavigil_platform.repository.specs.WorkReportFilterSpec;
import com.pharm.pharmavigil_platform.resources.workreport.CreateWorkReportRequest;
import com.pharm.pharmavigil_platform.resources.workreport.WorkReportResponse;
import com.pharm.pharmavigil_platform.security.IdentityProvider;
import com.pharm.pharmavigil_platform.usecases.workreport.CreateWorkReportUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkReportService {

    private final JpaWorkReportRepository jpaWorkReportRepository;
    private final CreateWorkReportUseCase createWorkReportUseCase;
    private final IdentityProvider identityProvider;
    private final WorkReportMapper workReportMapper;

    @Transactional
    public WorkReportResponse create(CreateWorkReportRequest request) {
        return workReportMapper.toResponse(createWorkReportUseCase.execute(workReportMapper.toDomain(request)));
    }

    public Page<WorkReportResponse> getAllStaffReports(WorkReportFilterSpec spec, Pageable pageable) {
        Specification<WorkReportEntity> userSpec = getUserSpec();
        Specification<WorkReportEntity> combined = spec == null ? userSpec : userSpec.and(spec);
        return jpaWorkReportRepository.findAll(combined, pageable).map(workReportMapper::toResponse);
    }

    public WorkReportResponse getById(UUID reportId) {
        return jpaWorkReportRepository.findByIdAndStaffUserId(reportId, identityProvider.getCurrentUser().id())
                .map(workReportMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Work report not found: " + reportId));
    }

    private Specification<WorkReportEntity> getUserSpec() {
        UUID userId = identityProvider.getCurrentUser().id();
        return (root, query, cb) -> cb.equal(root.get("staffUser").get("id"), userId);
    }
}
