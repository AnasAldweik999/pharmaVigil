package com.pharm.pharmavigil_platform.repository.adapter;

import com.pharm.pharmavigil_platform.domain.Department;
import com.pharm.pharmavigil_platform.domain.ExceptionalHolding;
import com.pharm.pharmavigil_platform.mapper.DepartmentMapper;
import com.pharm.pharmavigil_platform.repository.DepartmentRepository;
import com.pharm.pharmavigil_platform.repository.entities.DepartmentEntity;
import com.pharm.pharmavigil_platform.repository.entities.DepartmentExceptionalHoldingEntity;
import com.pharm.pharmavigil_platform.repository.entities.MachineEntity;
import com.pharm.pharmavigil_platform.repository.entities.UserEntity;
import com.pharm.pharmavigil_platform.repository.jpa.JpaDepartmentRepository;
import com.pharm.pharmavigil_platform.repository.jpa.JpaMachineRepository;
import com.pharm.pharmavigil_platform.repository.jpa.JpaProductRepository;
import com.pharm.pharmavigil_platform.repository.jpa.JpaUserRepository;
import com.pharm.pharmavigil_platform.repository.listing.DepartmentListing;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DepartmentRepositoryAdapter implements DepartmentRepository, DepartmentListing {

    private final JpaDepartmentRepository jpaDepartmentRepository;
    private final JpaMachineRepository jpaMachineRepository;
    private final JpaUserRepository jpaUserRepository;
    private final JpaProductRepository jpaProductRepository;
    private final DepartmentMapper departmentMapper;

    @Override
    public boolean existsByNameIgnoreCase(String name) {
        return jpaDepartmentRepository.existsByNameIgnoreCase(name);
    }

    @Override
    public boolean existsByNameIgnoreCaseAndIdNot(String name, UUID id) {
        return jpaDepartmentRepository.existsByNameIgnoreCaseAndIdNot(name, id);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaDepartmentRepository.existsById(id);
    }

    @Override
    public boolean existsByIdAndActiveTrue(UUID id) {
        return jpaDepartmentRepository.existsByIdAndActiveTrue(id);
    }

    @Override
    public Optional<Department> findById(UUID id) {
        return jpaDepartmentRepository.findById(id).map(departmentMapper::toDomain);
    }

    @Override
    @Transactional
    public Department save(Department department) {
        DepartmentEntity entity = departmentMapper.toEntity(department);

        // Set supervisors from IDs
        List<UserEntity> supervisors = new ArrayList<>();
        if (department.getSupervisorIds() != null) {
            department.getSupervisorIds().forEach(id -> supervisors.add(jpaUserRepository.getReferenceById(id)));
        }
        entity.setSupervisors(supervisors);

        // Set exceptional holdings with product references
        List<DepartmentExceptionalHoldingEntity> holdings = new ArrayList<>();
        if (department.getExceptionalHoldings() != null) {
            for (ExceptionalHolding h : department.getExceptionalHoldings()) {
                DepartmentExceptionalHoldingEntity holdingEntity = departmentMapper.toEntity(h);
                holdingEntity.setDepartment(entity);
                holdingEntity.setProduct(jpaProductRepository.getReferenceById(h.getProductId()));
                holdings.add(holdingEntity);
            }
        }
        entity.setExceptionalHoldings(holdings);

        DepartmentEntity saved = jpaDepartmentRepository.save(entity);

        // Handle machines — owner side is MachineEntity.department
        List<UUID> newMachineIds = department.getMachineIds() != null ? department.getMachineIds() : List.of();

        // Unlink machines previously in this department but no longer in the new list
        if (department.getId() != null) {
            jpaMachineRepository.findByDepartmentId(department.getId()).stream()
                    .filter(m -> !newMachineIds.contains(m.getId()))
                    .forEach(m -> {
                        m.setDepartment(null);
                        jpaMachineRepository.save(m);
                    });
        }

        // Link machines in the new list
        for (UUID machineId : newMachineIds) {
            MachineEntity machineEntity = jpaMachineRepository.findById(machineId).orElseThrow();
            machineEntity.setDepartment(saved);
            jpaMachineRepository.save(machineEntity);
        }

        return departmentMapper.toDomain(jpaDepartmentRepository.findById(saved.getId()).orElseThrow());
    }

    @Override
    public boolean existsByMachineId(UUID machineId) {
        return jpaDepartmentRepository.existsByMachines_Id(machineId);
    }

    @Override
    public boolean existsByMachineIdAndDepartmentIdNot(UUID machineId, UUID departmentId) {
        return jpaDepartmentRepository.existsByMachines_IdAndIdNot(machineId, departmentId);
    }

    @Override
    public boolean existsByIdAndMachineId(UUID departmentId, UUID machineId) {
        return jpaDepartmentRepository.existsByIdAndMachines_Id(departmentId, machineId);
    }

    @Override
    public boolean existsBySupervisorId(UUID userId) {
        return jpaDepartmentRepository.existsBySupervisors_Id(userId);
    }

    @Override
    public Page<Department> findAll(Specification<DepartmentEntity> spec, Pageable pageable) {
        return jpaDepartmentRepository.findAll(spec, pageable).map(departmentMapper::toDomain);
    }
}
