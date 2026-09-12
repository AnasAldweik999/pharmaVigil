package com.pharm.pharmavigil_platform.service;

import com.pharm.pharmavigil_platform.domain.Department;
import com.pharm.pharmavigil_platform.exception.ResourceNotFoundException;
import com.pharm.pharmavigil_platform.mapper.DepartmentMapper;
import com.pharm.pharmavigil_platform.repository.AllowedUnitsProvider;
import com.pharm.pharmavigil_platform.repository.adapter.DepartmentRepositoryAdapter;
import com.pharm.pharmavigil_platform.repository.entities.DepartmentEntity;
import com.pharm.pharmavigil_platform.repository.specs.DepartmentSpec;
import com.pharm.pharmavigil_platform.repository.specs.HoldingStatusSpecFactory;
import com.pharm.pharmavigil_platform.resources.department.CreateDepartmentRequest;
import com.pharm.pharmavigil_platform.resources.department.DepartmentResponse;
import com.pharm.pharmavigil_platform.resources.department.DepartmentWithBatchesResponse;
import com.pharm.pharmavigil_platform.resources.department.UpdateDepartmentRequest;
import com.pharm.pharmavigil_platform.usecases.department.CreateDepartmentUseCase;
import com.pharm.pharmavigil_platform.usecases.department.ToggleActiveDepartmentUseCase;
import com.pharm.pharmavigil_platform.usecases.department.UpdateDepartmentUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepositoryAdapter adapter;
    private final CreateDepartmentUseCase createDepartmentUseCase;
    private final UpdateDepartmentUseCase updateDepartmentUseCase;
    private final ToggleActiveDepartmentUseCase toggleActiveDepartmentUseCase;
    private final AllowedUnitsProvider allowedUnitsProvider;
    private final DepartmentMapper departmentMapper;
    private final HoldingStatusSpecFactory holdingStatusSpecFactory;

    public Page<DepartmentResponse> getAll(DepartmentSpec spec, Pageable pageable) {
        return adapter.findAll(spec, pageable).map(departmentMapper::toResponse);
    }

    public Page<DepartmentResponse> getActive(DepartmentSpec spec, Pageable pageable) {
        Specification<DepartmentEntity> combined = spec != null ? spec.and(isActive()) : Specification.where(isActive());
        return adapter.findAll(combined, pageable).map(departmentMapper::toResponse);
    }

    public Page<DepartmentWithBatchesResponse> getAllWithBatches(DepartmentSpec spec, UUID productId, String batchNo,
                                                                  String status, Pageable pageable) {
        Specification<DepartmentEntity> presence = holdingStatusSpecFactory.departmentHasBatchMatching(productId, batchNo, status);
        Specification<DepartmentEntity> combined = spec != null ? spec.and(presence) : Specification.where(presence);
        return adapter.findAll(combined, pageable).map(departmentMapper::toWithBatchesResponse);
    }

    public DepartmentResponse getById(UUID id) {
        return adapter.findById(id)
                .map(departmentMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("department.not.found"));
    }

    public List<String> getAllowedUnits() {
        return allowedUnitsProvider.getAllowedUnits();
    }

    @Transactional
    public DepartmentResponse create(CreateDepartmentRequest request) {
        Department department = departmentMapper.toDomain(request);
        Department created = createDepartmentUseCase.execute(department);
        return departmentMapper.toResponse(created);
    }

    @Transactional
    public DepartmentResponse update(UUID id, UpdateDepartmentRequest request) {
        Department existing = adapter.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("department.not.found"));
        Department department = departmentMapper.toDomain(request);
        department.setId(id);
        department.setActive(existing.isActive());
        department.setCreatedAt(existing.getCreatedAt());
        department.setCreatedBy(existing.getCreatedBy());
        Department updated = updateDepartmentUseCase.execute(department);
        return departmentMapper.toResponse(updated);
    }

    @Transactional
    public DepartmentResponse toggleActive(UUID id) {
        Department existing = adapter.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("department.not.found"));
        Department saved = toggleActiveDepartmentUseCase.execute(existing);
        return departmentMapper.toResponse(saved);
    }

    private Specification<DepartmentEntity> isActive() {
        return (root, query, cb) -> cb.isTrue(root.get("active"));
    }
}
