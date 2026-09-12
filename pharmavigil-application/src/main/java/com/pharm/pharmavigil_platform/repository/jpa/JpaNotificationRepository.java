package com.pharm.pharmavigil_platform.repository.jpa;

import com.pharm.pharmavigil_platform.repository.entities.NotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.UUID;

public interface JpaNotificationRepository extends JpaRepository<NotificationEntity, UUID> {

    long countByRecipientUserIdAndReadFalse(UUID recipientUserId);

    Page<NotificationEntity> findByRecipientUserId(UUID recipientUserId, Pageable pageable);

    Page<NotificationEntity> findByRecipientUserIdAndReadFalse(UUID recipientUserId, Pageable pageable);

    @Modifying
    @Query("UPDATE Notification n SET n.read = true, n.readAt = :readAt WHERE n.recipientUserId = :recipientUserId AND n.read = false")
    int markAllAsRead(UUID recipientUserId, Instant readAt);

    @Modifying
    @Query("DELETE FROM Notification n WHERE n.recipientUserId = :recipientUserId")
    int deleteAllByRecipientUserId(UUID recipientUserId);
}
