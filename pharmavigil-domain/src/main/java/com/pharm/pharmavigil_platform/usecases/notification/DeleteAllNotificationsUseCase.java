package com.pharm.pharmavigil_platform.usecases.notification;

import com.pharm.pharmavigil_platform.repository.NotificationRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public class DeleteAllNotificationsUseCase {

    private final NotificationRepository repository;

    public DeleteAllNotificationsUseCase(NotificationRepository repository) {
        this.repository = repository;
    }

    public void execute(UUID recipientUserId) {
        repository.deleteAllByRecipientUserId(recipientUserId);
        log.info("All notifications deleted for user '{}'", recipientUserId);
    }
}
