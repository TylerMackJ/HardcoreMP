package com.tylermackj.hardcoremp.interfaces;

import java.util.UUID;

import org.ladysnake.cca.api.v3.component.ComponentV3;

public interface IPlayerData extends ComponentV3 {
    UUID getAttemptUuid();
    void setAttemptUuid(UUID attemptUuid);
    void resetAttemptUuid();
}
