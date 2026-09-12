package com.pharm.pharmavigil_platform.service;

import com.pharm.pharmavigil_platform.domain.WorkReport;
import com.pharm.pharmavigil_platform.exception.ResourceNotFoundException;
import com.pharm.pharmavigil_platform.mapper.WorkReportMapper;
import com.pharm.pharmavigil_platform.repository.adapter.WorkReportRepositoryAdapter;
import com.pharm.pharmavigil_platform.repository.specs.SupervisorWorkReportFilterSpec;
import com.pharm.pharmavigil_platform.resources.supervisor.SupervisorWorkReportListResponse;
import com.pharm.pharmavigil_platform.resources.workreport.WorkReportResponse;
import com.pharm.pharmavigil_platform.usecases.workreport.DeleteWorkReportUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupervisorWorkReportService {

    private final WorkReportRepositoryAdapter workReportRepositoryAdapter;
    private final DeleteWorkReportUseCase deleteWorkReportUseCase;
    private final WorkReportMapper workReportMapper;

    private static final Map<String, String> REPORT_SORT_MAP = Map.of(
            "staffEmail", "staffUser.email",
            "staffName",  "staffUser.name"
    );

    public Page<SupervisorWorkReportListResponse> getAll(SupervisorWorkReportFilterSpec spec, Pageable pageable) {
        return workReportRepositoryAdapter.findAll(spec, remapReportSort(pageable))
                .map(workReportMapper::toListResponse);
    }

    public WorkReportResponse getById(UUID id) {
        return workReportRepositoryAdapter.findById(id)
                .map(workReportMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Work report not found: " + id));
    }

    @Transactional
    public void delete(UUID id) {
        deleteWorkReportUseCase.execute(WorkReport.builder().id(id).build());
    }

    private Pageable remapReportSort(Pageable pageable) {
        if (!pageable.getSort().isSorted()) return pageable;
        Sort remapped = Sort.by(
                pageable.getSort().stream()
                        .map(o -> o.withProperty(REPORT_SORT_MAP.getOrDefault(o.getProperty(), o.getProperty())))
                        .toList()
        );
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), remapped);
    }
}
