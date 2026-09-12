package com.pharm.pharmavigil_platform.repository;

import com.pharm.pharmavigil_platform.domain.AccountType;
import com.pharm.pharmavigil_platform.domain.HoldingAlertEmail;

/**
 * The single entry point for sending an email — every email-sending capability the app has,
 * regardless of which use case needs it. Mirrors {@link Notifier}'s role for in-app notifications.
 */
public interface Mailer {
    void sendPasswordResetEmail(String to, String token, AccountType accountType);
    void sendAccountInvitationEmail(String name, String to, String token, AccountType accountType);
    void sendHoldingAlertEmail(HoldingAlertEmail email);
}
