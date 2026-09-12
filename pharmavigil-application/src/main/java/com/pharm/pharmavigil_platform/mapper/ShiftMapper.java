package com.pharm.pharmavigil_platform.mapper;

import com.pharm.pharmavigil_platform.domain.Shift;
import com.pharm.pharmavigil_platform.repository.entities.ShiftEntity;
import com.pharm.pharmavigil_platform.resources.shift.CreateShiftRequest;
import com.pharm.pharmavigil_platform.resources.shift.ShiftResponse;
import com.pharm.pharmavigil_platform.resources.shift.UpdateShiftRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = StringMapper.class)
public interface ShiftMapper {

    @Mapping(target = "name", source = "name", qualifiedByName = "trim")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastUpdatedAt", ignore = true)
    @Mapping(target = "lastUpdatedBy", ignore = true)
    Shift toDomain(CreateShiftRequest request);

    @Mapping(target = "name", source = "name", qualifiedByName = "trim")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastUpdatedAt", ignore = true)
    @Mapping(target = "lastUpdatedBy", ignore = true)
    Shift toDomain(UpdateShiftRequest request);

    Shift toDomain(ShiftEntity entity);

    ShiftEntity toEntity(Shift shift);

    ShiftResponse toResponse(Shift shift);
}
