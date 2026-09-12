package com.pharm.pharmavigil_platform.messaging;

import com.pharm.pharmavigil_platform.domain.Notification;
import com.pharm.pharmavigil_platform.mapper.NotificationMapper;
import com.pharm.pharmavigil_platform.repository.NotificationPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketNotificationPublisher implements NotificationPublisher {

    private static final String DESTINATION = "/queue/notifications";

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationMapper notificationMapper;

    @Override
    public void publish(Notification notification) {
        // The notification is already durably saved by the time this runs (see NotificationPublisher's
        // contract) — a delivery failure here must never propagate and roll back that save, so it's
        // caught and logged rather than rethrown.
        try {
            messagingTemplate.convertAndSendToUser(
                    notification.getRecipientUserId().toString(),
                    DESTINATION,
                    notificationMapper.toResponse(notification));
        } catch (MessagingException e) {
            log.warn("Failed to push notification '{}' to user '{}' over WebSocket",
                    notification.getId(), notification.getRecipientUserId(), e);
        }
    }
}
