package com.pharm.pharmavigil_platform.service;

import com.pharm.pharmavigil_platform.domain.Shift;
import com.pharm.pharmavigil_platform.exception.ResourceNotFoundException;
import com.pharm.pharmavigil_platform.mapper.ShiftMapper;
import com.pharm.pharmavigil_platform.repository.ShiftRepository;
import com.pharm.pharmavigil_platform.repository.entities.ShiftEntity;
import com.pharm.pharmavigil_platform.repository.listing.ShiftListing;
import com.pharm.pharmavigil_platform.repository.specs.ShiftSpec;
import com.pharm.pharmavigil_platform.resources.shift.CreateShiftRequest;
import com.pharm.pharmavigil_platform.resources.shift.ShiftResponse;
import com.pharm.pharmavigil_platform.resources.shift.UpdateShiftRequest;
import com.pharm.pharmavigil_platform.usecases.shift.CreateShiftUseCase;
import com.pharm.pharmavigil_platform.usecases.shift.ToggleActiveShiftUseCase;
import com.pharm.pharmavigil_platform.usecases.shift.UpdateShiftUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShiftService {

    private final ShiftListing shiftListing;
    private final ShiftRepository shiftRepository;
    private final CreateShiftUseCase createShiftUseCase;
    private final UpdateShiftUseCase updateShiftUseCase;
    private final ToggleActiveShiftUseCase toggleActiveShiftUseCase;
    private final ShiftMapper shiftMapper;

    public Page<ShiftResponse> getAll(ShiftSpec spec, Pageable pageable) {
        return shiftListing.findAll(spec, pageable).map(shiftMapper::toResponse);
    }

    public Page<ShiftResponse> getActive(ShiftSpec spec, Pageable pageable) {
        Specification<ShiftEntity> combined = spec != null ? spec.and(isActive()) : Specification.where(isActive());
        return shiftListing.findAll(combined, pageable).map(shiftMapper::toResponse);
    }

    public ShiftResponse getById(UUID id) {
        return shiftMapper.toResponse(findOrThrow(id));
    }

    public Shift findOrThrow(UUID id) {
        return shiftRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found: " + id));
    }

    @Transactional
    public ShiftResponse create(CreateShiftRequest request) {
        return shiftMapper.toResponse(createShiftUseCase.execute(shiftMapper.toDomain(request)));
    }

    @Transactional
    public ShiftResponse update(UUID id, UpdateShiftRequest request) {
        Shift existing = findOrThrow(id);
        Shift shift = shiftMapper.toDomain(request);
        shift.setId(id);
        shift.setActive(existing.isActive());
        shift.setCreatedAt(existing.getCreatedAt());
        shift.setCreatedBy(existing.getCreatedBy());
        Shift updated = updateShiftUseCase.execute(shift);
        return shiftMapper.toResponse(updated);
    }

    @Transactional
    public ShiftResponse toggleActive(UUID id) {
        Shift shift = findOrThrow(id);
        Shift saved = toggleActiveShiftUseCase.execute(shift);
        return shiftMapper.toResponse(saved);
    }

    private Specification<ShiftEntity> isActive() {
        return (root, query, cb) -> cb.isTrue(root.get("active"));
    }
}
