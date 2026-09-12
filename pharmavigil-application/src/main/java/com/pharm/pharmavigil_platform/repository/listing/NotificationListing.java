package com.pharm.pharmavigil_platform.repository.listing;

import com.pharm.pharmavigil_platform.domain.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface NotificationListing {
    Page<Notification> findByRecipientUserId(UUID recipientUserId, boolean unreadOnly, Pageable pageable);
}
