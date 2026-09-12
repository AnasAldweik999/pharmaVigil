package com.pharm.pharmavigil_platform.mapper;

import com.pharm.pharmavigil_platform.domain.PasswordResetToken;
import com.pharm.pharmavigil_platform.repository.entities.PasswordResetTokenEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface PasswordResetTokenMapper {
    PasswordResetToken toDomain(PasswordResetTokenEntity entity);
    PasswordResetTokenEntity toEntity(PasswordResetToken token);
}
