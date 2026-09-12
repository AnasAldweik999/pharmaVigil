package com.pharm.pharmavigil_platform.resources.batch;

import java.time.LocalDateTime;

public record RejectBatchRequest(String reason, LocalDateTime lineClearanceAt) {}
