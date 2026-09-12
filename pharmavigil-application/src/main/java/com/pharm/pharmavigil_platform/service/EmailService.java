package com.pharm.pharmavigil_platform.service;

import com.pharm.pharmavigil_platform.domain.AccountType;
import com.pharm.pharmavigil_platform.domain.HoldingAlertEmail;
import com.pharm.pharmavigil_platform.repository.Mailer;
import jakarta.annotation.PostConstruct;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService implements Mailer {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    private String fromAddress;

    @Value("${app.base-url}")
    private String staffBaseUrl;

    @Value("${app.admin-base-url}")
    private String supervisorBaseUrl;

    private String invitationTemplate;
    private String passwordResetTemplate;
    private String holdingAlertTemplate;

    @PostConstruct
    private void loadTemplates() {
        invitationTemplate = loadTemplate("templates/email/invitation.html");
        passwordResetTemplate = loadTemplate("templates/email/password-reset.html");
        holdingAlertTemplate = loadTemplate("templates/email/holding-alert.html");
    }

    @Async
    @Override
    public void sendPasswordResetEmail(String to, String token, AccountType accountType) {
        String link = baseUrl(accountType) + "/reset-password?token=" + token;
        String html = passwordResetTemplate.replace("{{link}}", link);
        send(to, "Reset your PharmaVigil password", html);
    }

    @Async
    @Override
    public void sendAccountInvitationEmail(String name, String to, String token, AccountType accountType) {
        String link = baseUrl(accountType) + "/reset-password?token=" + token;
        String html = invitationTemplate
                .replace("{{name}}", name)
                .replace("{{link}}", link);
        send(to, "Welcome to PharmaVigil — Set Your Password", html);
    }

    @Async
    @Override
    public void sendHoldingAlertEmail(HoldingAlertEmail email) {
        boolean exceeded = "EXCEEDED".equals(email.severity());
        String ruleLabel = "GENERAL".equals(email.ruleType()) ? "General Holding Time" : "Department Holding Time";
        String thresholdNote = email.exceptionalThreshold()
                ? "product-specific exceptional threshold for this department"
                : "department's standard threshold";
        String html = holdingAlertTemplate
                .replace("{{recipientName}}", email.recipientName())
                .replace("{{batchNo}}", email.batchNo())
                .replace("{{productName}}", email.productName())
                .replace("{{departmentName}}", email.departmentName())
                .replace("{{ruleLabel}}", ruleLabel)
                .replace("{{ruleLabelLower}}", ruleLabel.toLowerCase())
                .replace("{{severityLabel}}", exceeded ? "EXCEEDED" : "Approaching Limit")
                .replace("{{severityVerb}}", exceeded ? "exceeded" : "nearly reached")
                .replace("{{daysElapsed}}", String.valueOf(email.daysElapsed()))
                .replace("{{thresholdDays}}", String.valueOf(email.thresholdDays()))
                .replace("{{thresholdNote}}", thresholdNote);
        send(email.recipientEmail(), "PharmaVigil — Batch " + email.batchNo() + " " + (exceeded ? "Exceeded" : "Alerted") + " Holding Time", html);
    }

    private void send(String to, String subject, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, StandardCharsets.UTF_8.name());
            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }

    private String baseUrl(AccountType accountType) {
        return accountType == AccountType.SUPERVISOR ? supervisorBaseUrl : staffBaseUrl;
    }

    private String loadTemplate(String path) {
        try (InputStream is = new ClassPathResource(path).getInputStream()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load email template: " + path, e);
        }
    }
}
