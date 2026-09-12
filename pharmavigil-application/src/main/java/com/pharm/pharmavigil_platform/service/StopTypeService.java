package com.pharm.pharmavigil_platform.service;

import com.pharm.pharmavigil_platform.domain.StopType;
import com.pharm.pharmavigil_platform.exception.ResourceNotFoundException;
import com.pharm.pharmavigil_platform.mapper.StopTypeMapper;
import com.pharm.pharmavigil_platform.repository.StopTypeRepository;
import com.pharm.pharmavigil_platform.repository.entities.StopTypeEntity;
import com.pharm.pharmavigil_platform.repository.listing.StopTypeListing;
import com.pharm.pharmavigil_platform.repository.specs.StopTypeSpec;
import com.pharm.pharmavigil_platform.resources.stoptype.CreateStopTypeRequest;
import com.pharm.pharmavigil_platform.resources.stoptype.StopTypeResponse;
import com.pharm.pharmavigil_platform.resources.stoptype.UpdateStopTypeRequest;
import com.pharm.pharmavigil_platform.usecases.stoptype.CreateStopTypeUseCase;
import com.pharm.pharmavigil_platform.usecases.stoptype.ToggleActiveStopTypeUseCase;
import com.pharm.pharmavigil_platform.usecases.stoptype.UpdateStopTypeUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StopTypeService {

    private final StopTypeListing stopTypeListing;
    private final StopTypeRepository stopTypeRepository;
    private final CreateStopTypeUseCase createStopTypeUseCase;
    private final UpdateStopTypeUseCase updateStopTypeUseCase;
    private final ToggleActiveStopTypeUseCase toggleActiveStopTypeUseCase;
    private final StopTypeMapper stopTypeMapper;

    public Page<StopTypeResponse> getAll(StopTypeSpec spec, Pageable pageable) {
        return stopTypeListing.findAll(spec, pageable).map(stopTypeMapper::toResponse);
    }

    public Page<StopTypeResponse> getActive(StopTypeSpec spec, Pageable pageable) {
        Specification<StopTypeEntity> combined = spec != null ? spec.and(isActive()) : Specification.where(isActive());
        return stopTypeListing.findAll(combined, pageable).map(stopTypeMapper::toResponse);
    }

    public StopTypeResponse getById(UUID id) {
        return stopTypeMapper.toResponse(findOrThrow(id));
    }

    @Transactional
    public StopTypeResponse create(CreateStopTypeRequest request) {
        return stopTypeMapper.toResponse(createStopTypeUseCase.execute(stopTypeMapper.toDomain(request)));
    }

    @Transactional
    public StopTypeResponse update(UUID id, UpdateStopTypeRequest request) {
        StopType existing = findOrThrow(id);
        StopType stopType = stopTypeMapper.toDomain(request);
        stopType.setId(id);
        stopType.setActive(existing.isActive());
        stopType.setCreatedAt(existing.getCreatedAt());
        stopType.setCreatedBy(existing.getCreatedBy());
        StopType updated = updateStopTypeUseCase.execute(stopType);
        return stopTypeMapper.toResponse(updated);
    }

    @Transactional
    public StopTypeResponse toggleActive(UUID id) {
        StopType stopType = findOrThrow(id);
        StopType saved = toggleActiveStopTypeUseCase.execute(stopType);
        return stopTypeMapper.toResponse(saved);
    }

    private StopType findOrThrow(UUID id) {
        return stopTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stop type not found: " + id));
    }

    private Specification<StopTypeEntity> isActive() {
        return (root, query, cb) -> cb.isTrue(root.get("active"));
    }
}
