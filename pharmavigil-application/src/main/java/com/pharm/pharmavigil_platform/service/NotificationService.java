package com.pharm.pharmavigil_platform.service;

import com.pharm.pharmavigil_platform.domain.Notification;
import com.pharm.pharmavigil_platform.exception.ResourceNotFoundException;
import com.pharm.pharmavigil_platform.mapper.NotificationMapper;
import com.pharm.pharmavigil_platform.repository.adapter.NotificationRepositoryAdapter;
import com.pharm.pharmavigil_platform.resources.notification.NotificationResponse;
import com.pharm.pharmavigil_platform.security.IdentityProvider;
import com.pharm.pharmavigil_platform.usecases.notification.DeleteAllNotificationsUseCase;
import com.pharm.pharmavigil_platform.usecases.notification.MarkAllNotificationsReadUseCase;
import com.pharm.pharmavigil_platform.usecases.notification.MarkNotificationReadUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepositoryAdapter adapter;
    private final MarkNotificationReadUseCase markNotificationReadUseCase;
    private final MarkAllNotificationsReadUseCase markAllNotificationsReadUseCase;
    private final DeleteAllNotificationsUseCase deleteAllNotificationsUseCase;
    private final NotificationMapper notificationMapper;
    private final IdentityProvider identityProvider;

    public Page<NotificationResponse> getMine(boolean unreadOnly, Pageable pageable) {
        UUID userId = identityProvider.getCurrentUser().id();
        return adapter.findByRecipientUserId(userId, unreadOnly, pageable).map(notificationMapper::toResponse);
    }

    public UUID currentUserId() {
        return identityProvider.getCurrentUser().id();
    }

    public long getUnreadCount() {
        return adapter.countUnreadByRecipientUserId(currentUserId());
    }

    @Transactional
    public NotificationResponse markAsRead(UUID id) {
        Notification notification = adapter.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + id));
        Notification updated = markNotificationReadUseCase.execute(notification, currentUserId());
        return notificationMapper.toResponse(updated);
    }

    @Transactional
    public void markAllAsRead() {
        markAllNotificationsReadUseCase.execute(currentUserId());
    }

    @Transactional
    public void deleteAll() {
        deleteAllNotificationsUseCase.execute(currentUserId());
    }
}
