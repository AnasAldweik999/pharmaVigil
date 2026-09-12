package com.pharm.pharmavigil_platform.usecases.notification;

import com.pharm.pharmavigil_platform.domain.Notification;
import com.pharm.pharmavigil_platform.repository.NotificationRepository;
import com.pharm.pharmavigil_platform.validators.exceptions.SystemViolationException;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.UUID;

@Slf4j
public class MarkNotificationReadUseCase {

    private final NotificationRepository repository;

    public MarkNotificationReadUseCase(NotificationRepository repository) {
        this.repository = repository;
    }

    public Notification execute(Notification notification, UUID requestingUserId) {
        if (!notification.getRecipientUserId().equals(requestingUserId)) {
            throw new SystemViolationException("id", "notification.not.owned");
        }
        if (notification.isRead()) {
            return notification;
        }
        notification.setRead(true);
        notification.setReadAt(Instant.now());
        Notification saved = repository.save(notification);
        log.info("Notification '{}' marked as read by user '{}'", saved.getId(), requestingUserId);
        return saved;
    }
}
