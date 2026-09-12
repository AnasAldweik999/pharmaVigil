package com.pharm.pharmavigil_platform.domain;

import java.util.UUID;

public record BatchLogEntryDeletion(Batch batch, UUID entryId) {}
