package com.pharm.pharmavigil_platform.usecases.batch;

import com.pharm.pharmavigil_platform.domain.*;
import com.pharm.pharmavigil_platform.repository.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Scheduled evaluation of holding-time rules for every currently-running batch: general holding
 * time (since first log), current-department holding time (department standard, or a
 * product-specific exceptional override if configured). Notifies every active supervisor of the
 * batch's current department, once per false-&gt;true state transition, via both in-app notification
 * and email.
 */
@Slf4j
public class EvaluateBatchHoldingUseCase {

    private final List<BatchStatus> runningStatuses = List.of(BatchStatus.IN_PROGRESS);

    private final BatchRepository batchRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final Notifier notifier;
    private final Mailer mailer;
    private final GeneralHoldingThresholdProvider generalHoldingThresholdProvider;
    private final HoldingAlertEmailProvider holdingAlertEmailProvider;
    private final EffectiveHoldingTime effectiveHoldingTime;

    public EvaluateBatchHoldingUseCase(BatchRepository batchRepository,
                                        DepartmentRepository departmentRepository,
                                        UserRepository userRepository,
                                        Notifier notifier,
                                        Mailer mailer,
                                        GeneralHoldingThresholdProvider generalHoldingThresholdProvider,
                                        HoldingAlertEmailProvider holdingAlertEmailProvider,
                                        EffectiveHoldingTime effectiveHoldingTime) {
        this.batchRepository = batchRepository;
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
        this.notifier = notifier;
        this.mailer = mailer;
        this.generalHoldingThresholdProvider = generalHoldingThresholdProvider;
        this.holdingAlertEmailProvider = holdingAlertEmailProvider;
        this.effectiveHoldingTime = effectiveHoldingTime;
    }

    public void execute() {
        int generalThreshold = generalHoldingThresholdProvider.getGeneralHoldingThresholdDays();
        LocalDate today = LocalDate.now();
        List<Batch> runningBatches = batchRepository.findAllByStatusIn(runningStatuses);
        log.info("Evaluating holding time for {} running batches", runningBatches.size());

        for (Batch batch : runningBatches) {
            evaluate(batch, generalThreshold, today);
        }
    }

    private void evaluate(Batch batch, int generalThreshold, LocalDate today) {
        boolean newGeneralAlerted = false;
        boolean newGeneralExceeded = false;
        long daysSinceFirstLog = 0;
        if (batch.getFirstLoggedAt() != null) {
            daysSinceFirstLog = ChronoUnit.DAYS.between(batch.getFirstLoggedAt().toLocalDate(), today);
            newGeneralExceeded = daysSinceFirstLog >= generalThreshold;
            newGeneralAlerted = daysSinceFirstLog >= generalThreshold - 1;
        }

        boolean newDeptAlerted = false;
        boolean newDeptExceeded = false;
        long daysInDept = 0;
        int effectiveThreshold = 0;
        boolean exceptional = false;
        Department department = batch.getCurrentDepartmentId() != null
                ? departmentRepository.findById(batch.getCurrentDepartmentId()).orElse(null) : null;
        if (department != null && batch.getCurrentDepartmentEnteredAt() != null) {
            ExceptionalHolding holding = effectiveHoldingTime.findExceptional(department, batch.getProductId());
            effectiveThreshold = holding != null ? holding.getHoldingTimeDays() : department.getStandardHoldingTime();
            exceptional = holding != null;
            daysInDept = ChronoUnit.DAYS.between(batch.getCurrentDepartmentEnteredAt().toLocalDate(), today);
            newDeptExceeded = daysInDept >= effectiveThreshold;
            newDeptAlerted = daysInDept >= effectiveThreshold - 1;
        }

        boolean generalExceededFlip = newGeneralExceeded && !batch.isGeneralHoldingExceeded();
        boolean generalAlertedFlip = !generalExceededFlip && newGeneralAlerted && !batch.isGeneralHoldingAlerted();
        boolean deptExceededFlip = newDeptExceeded && !batch.isDepartmentHoldingExceeded();
        boolean deptAlertedFlip = !deptExceededFlip && newDeptAlerted && !batch.isDepartmentHoldingAlerted();

        batchRepository.updateHoldingFlags(batch.getId(), newGeneralAlerted, newGeneralExceeded, newDeptAlerted, newDeptExceeded);

        if (department == null || !(generalExceededFlip || generalAlertedFlip || deptExceededFlip || deptAlertedFlip)) {
            return;
        }

        List<User> supervisors = department.getSupervisorIds() == null || department.getSupervisorIds().isEmpty()
                ? List.of()
                : userRepository.findAllByIdIn(department.getSupervisorIds()).stream()
                        .filter(u -> u.getStatus() == UserStatus.ACTIVE)
                        .toList();

        if (generalExceededFlip) {
            notify(batch, department, supervisors, "GENERAL", "EXCEEDED", daysSinceFirstLog, generalThreshold, false);
        } else if (generalAlertedFlip) {
            notify(batch, department, supervisors, "GENERAL", "ALERTED", daysSinceFirstLog, generalThreshold, false);
        }

        if (deptExceededFlip) {
            notify(batch, department, supervisors, "DEPARTMENT", "EXCEEDED", daysInDept, effectiveThreshold, exceptional);
        } else if (deptAlertedFlip) {
            notify(batch, department, supervisors, "DEPARTMENT", "ALERTED", daysInDept, effectiveThreshold, exceptional);
        }
    }

    private void notify(Batch batch, Department department, List<User> supervisors, String ruleType, String severity,
                         long daysElapsed, int thresholdDays, boolean exceptional) {
        String ruleLabel = "GENERAL".equals(ruleType) ? "general" : "department";
        for (User supervisor : supervisors) {
            notifier.send(NotifyRequest.builder()
                    .audience(NotificationAudience.SPECIFIC_USER)
                    .specificUserId(supervisor.getId())
                    .type("BATCH_HOLDING_" + severity)
                    .title("Batch " + batch.getBatchNo() + " — " + ruleLabel + " holding time " + severity.toLowerCase())
                    .message("Batch " + batch.getBatchNo() + " (" + batch.getProductName() + ") in department "
                            + department.getName() + " has " + (severity.equals("EXCEEDED") ? "exceeded" : "nearly reached")
                            + " its " + ruleLabel + " holding time limit: " + daysElapsed + " of " + thresholdDays + " days.")
                    .entityType("BATCH")
                    .entityId(batch.getId().toString())
                    .build());


            if (holdingAlertEmailProvider.isHoldingAlertEmailEnabled()) {
                mailer.sendHoldingAlertEmail(new HoldingAlertEmail(supervisor.getEmail(), supervisor.getName(),
                        batch.getBatchNo(), batch.getProductName(), department.getName(),
                        ruleType, severity, daysElapsed, thresholdDays, exceptional));
            }
        }
    }
}
