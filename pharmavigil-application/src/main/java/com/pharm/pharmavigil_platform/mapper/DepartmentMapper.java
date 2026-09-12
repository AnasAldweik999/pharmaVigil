package com.pharm.pharmavigil_platform.mapper;

import com.pharm.pharmavigil_platform.domain.Department;
import com.pharm.pharmavigil_platform.domain.ExceptionalHolding;
import com.pharm.pharmavigil_platform.repository.entities.DepartmentEntity;
import com.pharm.pharmavigil_platform.repository.entities.DepartmentExceptionalHoldingEntity;
import com.pharm.pharmavigil_platform.repository.entities.MachineEntity;
import com.pharm.pharmavigil_platform.repository.entities.UserEntity;
import com.pharm.pharmavigil_platform.resources.department.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = StringMapper.class)
public interface DepartmentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastUpdatedAt", ignore = true)
    @Mapping(target = "lastUpdatedBy", ignore = true)
    @Mapping(target = "name", source = "name", qualifiedByName = "trim")
    Department toDomain(CreateDepartmentRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastUpdatedAt", ignore = true)
    @Mapping(target = "lastUpdatedBy", ignore = true)
    @Mapping(target = "name", source = "name", qualifiedByName = "trim")
    Department toDomain(UpdateDepartmentRequest request);

    @Mapping(target = "machineIds", source = "machines")
    @Mapping(target = "supervisorIds", source = "supervisors")
    Department toDomain(DepartmentEntity entity);

    @Mapping(target = "exceptionalHoldings", ignore = true)
    @Mapping(target = "machines", ignore = true)
    @Mapping(target = "supervisors", ignore = true)
    DepartmentEntity toEntity(Department department);

    @Mapping(target = "productId", source = "product.id")
    ExceptionalHolding toDomain(DepartmentExceptionalHoldingEntity entity);

    @Mapping(target = "department", ignore = true)
    @Mapping(target = "product", ignore = true)
    DepartmentExceptionalHoldingEntity toEntity(ExceptionalHolding holding);

    @Mapping(target = "id", ignore = true)
    ExceptionalHolding toDomain(ExceptionalHoldingRequest request);

    DepartmentResponse toResponse(Department department);

    DepartmentWithBatchesResponse toWithBatchesResponse(Department department);

    ExceptionalHoldingResponse toResponse(ExceptionalHolding holding);

    default List<UUID> machinesToIds(List<MachineEntity> machines) {
        if (machines == null) return null;
        return machines.stream().map(MachineEntity::getId).collect(Collectors.toList());
    }

    default List<UUID> supervisorsToIds(List<UserEntity> supervisors) {
        if (supervisors == null) return null;
        return supervisors.stream().map(UserEntity::getId).collect(Collectors.toList());
    }
}
