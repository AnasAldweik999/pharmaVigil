package com.pharm.pharmavigil_platform.repository;

import com.pharm.pharmavigil_platform.domain.Notification;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository {
    Notification save(Notification notification);
    List<Notification> saveAll(List<Notification> notifications);
    Optional<Notification> findById(UUID id);
    long countUnreadByRecipientUserId(UUID recipientUserId);
    void markAllAsReadByRecipientUserId(UUID recipientUserId, Instant readAt);
    void deleteAllByRecipientUserId(UUID recipientUserId);
}
