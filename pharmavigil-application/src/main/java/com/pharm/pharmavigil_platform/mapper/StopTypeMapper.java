package com.pharm.pharmavigil_platform.mapper;

import com.pharm.pharmavigil_platform.domain.StopType;
import com.pharm.pharmavigil_platform.repository.entities.StopTypeEntity;
import com.pharm.pharmavigil_platform.resources.stoptype.CreateStopTypeRequest;
import com.pharm.pharmavigil_platform.resources.stoptype.StopTypeResponse;
import com.pharm.pharmavigil_platform.resources.stoptype.UpdateStopTypeRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = StringMapper.class)
public interface StopTypeMapper {

    @Mapping(target = "name", source = "name", qualifiedByName = "trim")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastUpdatedAt", ignore = true)
    @Mapping(target = "lastUpdatedBy", ignore = true)
    StopType toDomain(CreateStopTypeRequest request);

    @Mapping(target = "name", source = "name", qualifiedByName = "trim")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastUpdatedAt", ignore = true)
    @Mapping(target = "lastUpdatedBy", ignore = true)
    StopType toDomain(UpdateStopTypeRequest request);

    StopType toDomain(StopTypeEntity entity);

    StopTypeResponse toResponse(StopType stopType);
}
