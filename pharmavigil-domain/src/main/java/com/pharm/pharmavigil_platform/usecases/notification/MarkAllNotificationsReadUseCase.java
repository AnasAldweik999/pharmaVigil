package com.pharm.pharmavigil_platform.usecases.notification;

import com.pharm.pharmavigil_platform.repository.NotificationRepository;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.UUID;

@Slf4j
public class MarkAllNotificationsReadUseCase {

    private final NotificationRepository repository;

    public MarkAllNotificationsReadUseCase(NotificationRepository repository) {
        this.repository = repository;
    }

    public void execute(UUID recipientUserId) {
        repository.markAllAsReadByRecipientUserId(recipientUserId, Instant.now());
        log.info("All notifications marked as read for user '{}'", recipientUserId);
    }
}
