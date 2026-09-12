package com.pharm.pharmavigil_platform.config.notification;

import com.pharm.pharmavigil_platform.repository.NotificationRepository;
import com.pharm.pharmavigil_platform.usecases.notification.DeleteAllNotificationsUseCase;
import com.pharm.pharmavigil_platform.usecases.notification.MarkAllNotificationsReadUseCase;
import com.pharm.pharmavigil_platform.usecases.notification.MarkNotificationReadUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Raising a notification (single user or a whole audience) goes through the Notifier port
 * instead — see the Notifier interface and its sole implementation, NotifierImpl. This config is
 * only for the read-side use cases below (marking read, deleting).
 */
@Configuration
public class NotificationUseCaseConfig {

    @Bean
    public MarkNotificationReadUseCase markNotificationReadUseCase(NotificationRepository notificationRepository) {
        return new MarkNotificationReadUseCase(notificationRepository);
    }

    @Bean
    public MarkAllNotificationsReadUseCase markAllNotificationsReadUseCase(NotificationRepository notificationRepository) {
        return new MarkAllNotificationsReadUseCase(notificationRepository);
    }

    @Bean
    public DeleteAllNotificationsUseCase deleteAllNotificationsUseCase(NotificationRepository notificationRepository) {
        return new DeleteAllNotificationsUseCase(notificationRepository);
    }
}
