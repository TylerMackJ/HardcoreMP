package com.tylermackj.hardcoremp.injects;

import java.util.UUID;

public interface PlayerExtension {
    default void setAttempt(UUID attempt) {}
    default UUID getAttempt() { return UUID.randomUUID(); }
}
