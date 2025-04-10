package edu.cantuario2.persistence.entity;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class BlockEntity {
    private Long id;
    private OffsetDateTime blockedAt;
    private String blockReason;
    private OffsetDateTime unlockedAt;
    private String unlockReason;
}
