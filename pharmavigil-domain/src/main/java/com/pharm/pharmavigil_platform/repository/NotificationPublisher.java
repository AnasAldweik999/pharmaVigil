package com.pharm.pharmavigil_platform.repository;

import com.pharm.pharmavigil_platform.domain.Notification;

/**
 * Pushes a just-created notification to its recipient in real time, if they're currently
 * connected. This is a delivery side-effect only — the notification is already durably saved
 * by the time this runs, so a missed push (recipient offline) is never data loss; the client
 * picks it up via the regular REST list/unread-count on its next fetch.
 */
public interface NotificationPublisher {
    void publish(Notification notification);
}
