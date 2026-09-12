package com.pharm.pharmavigil_platform.scheduler;

import com.pharm.pharmavigil_platform.usecases.batch.EvaluateBatchHoldingUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Periodically evaluates every currently-running batch's holding time (general and
 * current-department) and notifies department supervisors on state transitions. Runs inside an
 * explicit transaction because, unlike an HTTP request, a {@code @Scheduled} method has no
 * open-in-view session — evaluating a department's lazy machines/supervisors collections requires
 * one to be opened here.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BatchHoldingAlertScheduler {

    private final EvaluateBatchHoldingUseCase evaluateBatchHoldingUseCase;

    @Scheduled(cron = "${app.holding.check-cron}")
    @Transactional
    public void run() {
        log.info("Running scheduled batch holding-time evaluation");
        evaluateBatchHoldingUseCase.execute();
        log.info("Batch holding-time evaluation complete");
    }
}
