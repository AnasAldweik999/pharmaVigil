package com.pharm.pharmavigil_platform.repository.adapter;

import com.pharm.pharmavigil_platform.domain.Shift;
import com.pharm.pharmavigil_platform.mapper.ShiftMapper;
import com.pharm.pharmavigil_platform.repository.ShiftRepository;
import com.pharm.pharmavigil_platform.repository.entities.ShiftEntity;
import com.pharm.pharmavigil_platform.repository.jpa.JpaShiftRepository;
import com.pharm.pharmavigil_platform.repository.listing.ShiftListing;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ShiftRepositoryAdapter implements ShiftRepository, ShiftListing {

    private final JpaShiftRepository jpaShiftRepository;
    private final ShiftMapper shiftMapper;

    @Override
    public boolean existsByNameIgnoreCase(String name) {
        return jpaShiftRepository.existsByNameIgnoreCase(name);
    }

    @Override
    public boolean existsByNameIgnoreCaseAndIdNot(String name, UUID id) {
        return jpaShiftRepository.existsByNameIgnoreCaseAndIdNot(name, id);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaShiftRepository.existsById(id);
    }

    @Override
    public boolean existsByIdAndActiveTrue(UUID id) {
        return jpaShiftRepository.existsByIdAndActiveTrue(id);
    }

    @Override
    public Optional<Shift> findById(UUID id) {
        return jpaShiftRepository.findById(id).map(shiftMapper::toDomain);
    }

    @Override
    public List<Shift> findAll() {
        return jpaShiftRepository.findAll().stream().map(shiftMapper::toDomain).toList();
    }

    @Override
    public Shift save(Shift shift) {
        return shiftMapper.toDomain(jpaShiftRepository.save(shiftMapper.toEntity(shift)));
    }

    @Override
    public Page<Shift> findAll(Specification<ShiftEntity> spec, Pageable pageable) {
        return jpaShiftRepository.findAll(spec, pageable).map(shiftMapper::toDomain);
    }
}
