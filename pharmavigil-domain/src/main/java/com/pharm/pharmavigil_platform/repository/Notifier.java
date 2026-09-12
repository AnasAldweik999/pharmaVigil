package com.pharm.pharmavigil_platform.repository;

import com.pharm.pharmavigil_platform.domain.Notification;
import com.pharm.pharmavigil_platform.domain.NotifyRequest;

import java.util.List;

/**
 * The single entry point for raising an in-app notification — one specific user or a whole
 * audience (see NotificationAudience), through the same method. Inject this directly into
 * whichever use case or service, domain or application, needs to notify someone as a side effect
 * of its own logic, e.g. RejectBatchUseCase calling
 * notifier.send(NotifyRequest.builder().audience(SPECIFIC_USER).specificUserId(staffUserId)
 *     .type("BATCH_REJECTED").title(...).message(...).entityType("BATCH").entityId(batchId).build()).
 */
public interface Notifier {
    List<Notification> send(NotifyRequest request);
}
