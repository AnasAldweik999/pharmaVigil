package com.pharm.pharmavigil_platform.mapper;

import com.pharm.pharmavigil_platform.domain.Machine;
import com.pharm.pharmavigil_platform.repository.entities.MachineEntity;
import com.pharm.pharmavigil_platform.resources.machine.CreateMachineRequest;
import com.pharm.pharmavigil_platform.resources.machine.MachineResponse;
import com.pharm.pharmavigil_platform.resources.machine.UpdateMachineRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = StringMapper.class)
public interface MachineMapper {

    @Mapping(target = "name", source = "name", qualifiedByName = "trim")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastUpdatedAt", ignore = true)
    @Mapping(target = "lastUpdatedBy", ignore = true)
    Machine toDomain(CreateMachineRequest request);

    @Mapping(target = "name", source = "name", qualifiedByName = "trim")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastUpdatedAt", ignore = true)
    @Mapping(target = "lastUpdatedBy", ignore = true)
    Machine toDomain(UpdateMachineRequest request);

    Machine toDomain(MachineEntity entity);

    MachineResponse toResponse(Machine machine);
}
