package com.pharm.pharmavigil_platform.mapper;

import com.pharm.pharmavigil_platform.domain.Notification;
import com.pharm.pharmavigil_platform.repository.entities.NotificationEntity;
import com.pharm.pharmavigil_platform.resources.notification.NotificationResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    Notification toDomain(NotificationEntity entity);

    NotificationResponse toResponse(Notification notification);
}
