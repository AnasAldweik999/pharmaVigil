package com.pharm.pharmavigil_platform.mapper;

import com.pharm.pharmavigil_platform.domain.User;
import com.pharm.pharmavigil_platform.repository.entities.UserEntity;
import com.pharm.pharmavigil_platform.resources.user.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    User toDomain(UserEntity entity);
    UserEntity toEntity(User user);
    UserResponse toResponse(User user);
    UserResponse toResponse(UserEntity entity);
}
