package com.pharm.pharmavigil_platform.notification;

import com.pharm.pharmavigil_platform.domain.AccountType;
import com.pharm.pharmavigil_platform.domain.Notification;
import com.pharm.pharmavigil_platform.domain.NotificationAudience;
import com.pharm.pharmavigil_platform.domain.NotifyRequest;
import com.pharm.pharmavigil_platform.domain.User;
import com.pharm.pharmavigil_platform.domain.UserStatus;
import com.pharm.pharmavigil_platform.repository.NotificationPublisher;
import com.pharm.pharmavigil_platform.repository.NotificationRepository;
import com.pharm.pharmavigil_platform.repository.Notifier;
import com.pharm.pharmavigil_platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * The sole implementation of Notifier: resolves the request's audience to currently-active
 * recipients (excluding inactive accounts regardless of audience), saves the whole batch in one
 * transaction (NotificationRepository.saveAll), then pushes each saved notification to its
 * recipient. Every caller — domain use case or application service — goes through this same path
 * whether it's notifying one person or a whole audience.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotifierImpl implements Notifier {

    private final NotificationRepository notificationRepository;
    private final NotificationPublisher notificationPublisher;
    private final UserRepository userRepository;

    @Override
    public List<Notification> send(NotifyRequest request) {
        List<UUID> recipientIds = resolveActiveRecipientIds(request.audience(), request.specificUserId());
        log.info("Notifying {} recipient(s) — type '{}', audience '{}'",
                recipientIds.size(), request.type(), request.audience());
        if (recipientIds.isEmpty()) {
            return List.of();
        }

        List<Notification> toSave = recipientIds.stream()
                .map(recipientId -> Notification.builder()
                        .recipientUserId(recipientId)
                        .type(request.type())
                        .title(request.title())
                        .message(request.message())
                        .entityType(request.entityType())
                        .entityId(request.entityId())
                        .read(false)
                        .build())
                .toList();

        List<Notification> saved = notificationRepository.saveAll(toSave);
        saved.forEach(notificationPublisher::publish);
        return saved;
    }

    private List<UUID> resolveActiveRecipientIds(NotificationAudience audience, UUID specificUserId) {
        return switch (audience) {
            case SPECIFIC_USER -> userRepository.findById(specificUserId)
                    .filter(user -> user.getStatus() == UserStatus.ACTIVE)
                    .map(user -> List.of(user.getId()))
                    .orElse(List.of());
            case ALL_SUPERVISORS -> userRepository.findActiveByAccountType(AccountType.SUPERVISOR).stream().map(User::getId).toList();
            case ALL_STAFF -> userRepository.findActiveByAccountType(AccountType.STAFF).stream().map(User::getId).toList();
            case ALL_USERS -> userRepository.findAllActive().stream().map(User::getId).toList();
        };
    }
}
