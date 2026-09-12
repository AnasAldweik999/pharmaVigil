package com.pharm.pharmavigil_platform.repository.adapter;

import com.pharm.pharmavigil_platform.domain.StopType;
import com.pharm.pharmavigil_platform.mapper.StopTypeMapper;
import com.pharm.pharmavigil_platform.repository.StopTypeRepository;
import com.pharm.pharmavigil_platform.repository.entities.StopTypeEntity;
import com.pharm.pharmavigil_platform.repository.jpa.JpaStopTypeRepository;
import com.pharm.pharmavigil_platform.repository.listing.StopTypeListing;
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
public class StopTypeRepositoryAdapter implements StopTypeRepository, StopTypeListing {

    private final JpaStopTypeRepository jpaStopTypeRepository;
    private final StopTypeMapper stopTypeMapper;

    @Override
    public boolean existsByNameIgnoreCase(String name) {
        return jpaStopTypeRepository.existsByNameIgnoreCase(name);
    }

    @Override
    public boolean existsByNameIgnoreCaseAndIdNot(String name, UUID id) {
        return jpaStopTypeRepository.existsByNameIgnoreCaseAndIdNot(name, id);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaStopTypeRepository.existsById(id);
    }

    @Override
    public Optional<StopType> findById(UUID id) {
        return jpaStopTypeRepository.findById(id).map(stopTypeMapper::toDomain);
    }

    @Override
    public List<StopType> findAll() {
        return jpaStopTypeRepository.findAll().stream().map(stopTypeMapper::toDomain).toList();
    }

    @Override
    public StopType save(StopType stopType) {
        // Load-then-patch rather than stopTypeMapper.toEntity(stopType) directly, so an update
        // never has to worry about a domain field carrying a stale/omitted value overwriting a
        // column it doesn't know about (mirrors MachineRepositoryAdapter's approach).
        StopTypeEntity entity = stopType.getId() != null
                ? jpaStopTypeRepository.findById(stopType.getId()).orElseThrow()
                : new StopTypeEntity();
        entity.setName(stopType.getName());
        entity.setActive(stopType.isActive());
        entity.setLastUpdatedAt(stopType.getLastUpdatedAt());
        entity.setLastUpdatedBy(stopType.getLastUpdatedBy());
        if (stopType.getId() == null) {
            entity.setCreatedBy(stopType.getCreatedBy());
        }
        StopTypeEntity saved = jpaStopTypeRepository.save(entity);
        return stopTypeMapper.toDomain(saved);
    }

    @Override
    public Page<StopType> findAll(Specification<StopTypeEntity> spec, Pageable pageable) {
        return jpaStopTypeRepository.findAll(spec, pageable).map(stopTypeMapper::toDomain);
    }
}
