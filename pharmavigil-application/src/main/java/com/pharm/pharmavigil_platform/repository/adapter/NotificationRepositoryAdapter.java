package com.pharm.pharmavigil_platform.repository.adapter;

import com.pharm.pharmavigil_platform.domain.Notification;
import com.pharm.pharmavigil_platform.mapper.NotificationMapper;
import com.pharm.pharmavigil_platform.repository.NotificationRepository;
import com.pharm.pharmavigil_platform.repository.entities.NotificationEntity;
import com.pharm.pharmavigil_platform.repository.jpa.JpaNotificationRepository;
import com.pharm.pharmavigil_platform.repository.listing.NotificationListing;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NotificationRepositoryAdapter implements NotificationRepository, NotificationListing {

    private final JpaNotificationRepository jpaNotificationRepository;
    private final NotificationMapper notificationMapper;

    @Override
    public Notification save(Notification notification) {
        NotificationEntity entity = notification.getId() != null
                ? jpaNotificationRepository.findById(notification.getId()).orElseThrow()
                : new NotificationEntity();
        entity.setRecipientUserId(notification.getRecipientUserId());
        entity.setType(notification.getType());
        entity.setTitle(notification.getTitle());
        entity.setMessage(notification.getMessage());
        entity.setEntityType(notification.getEntityType());
        entity.setEntityId(notification.getEntityId());
        entity.setRead(notification.isRead());
        entity.setReadAt(notification.getReadAt());
        NotificationEntity saved = jpaNotificationRepository.save(entity);
        return notificationMapper.toDomain(saved);
    }

    @Override
    @Transactional
    public List<Notification> saveAll(List<Notification> notifications) {
        // One transaction for the whole batch — broadcasting to an audience of hundreds
        // (e.g. ALL_STAFF) must not cost hundreds of separate round-trip commits.
        List<NotificationEntity> entities = notifications.stream()
                .map(notification -> NotificationEntity.builder()
                        .recipientUserId(notification.getRecipientUserId())
                        .type(notification.getType())
                        .title(notification.getTitle())
                        .message(notification.getMessage())
                        .entityType(notification.getEntityType())
                        .entityId(notification.getEntityId())
                        .read(notification.isRead())
                        .build())
                .toList();
        // saveAllAndFlush (not saveAll) matters here: @CreationTimestamp only populates
        // createdAt on the entity at flush time, and the mapped Notification below is
        // handed straight to NotificationPublisher for a live push — without forcing the
        // flush first, that push would carry a null createdAt (whatever REST fetches
        // afterwards look correct, since those re-read the row post-commit).
        return jpaNotificationRepository.saveAllAndFlush(entities).stream()
                .map(notificationMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Notification> findById(UUID id) {
        return jpaNotificationRepository.findById(id).map(notificationMapper::toDomain);
    }

    @Override
    public long countUnreadByRecipientUserId(UUID recipientUserId) {
        return jpaNotificationRepository.countByRecipientUserIdAndReadFalse(recipientUserId);
    }

    @Override
    public void markAllAsReadByRecipientUserId(UUID recipientUserId, Instant readAt) {
        jpaNotificationRepository.markAllAsRead(recipientUserId, readAt);
    }

    @Override
    public void deleteAllByRecipientUserId(UUID recipientUserId) {
        jpaNotificationRepository.deleteAllByRecipientUserId(recipientUserId);
    }

    @Override
    public Page<Notification> findByRecipientUserId(UUID recipientUserId, boolean unreadOnly, Pageable pageable) {
        Page<NotificationEntity> page = unreadOnly
                ? jpaNotificationRepository.findByRecipientUserIdAndReadFalse(recipientUserId, pageable)
                : jpaNotificationRepository.findByRecipientUserId(recipientUserId, pageable);
        return page.map(notificationMapper::toDomain);
    }
}
