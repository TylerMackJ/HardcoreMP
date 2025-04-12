package com.tylermackj.hardcoremp.injects;

import java.util.UUID;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface TeamExtension {
    default int getAttempt() { return -1; }
    default UUID getAttemptUuid() { return UUID.randomUUID(); }
    default void randomizeSpawnPosForWorld(World world) {}
    default BlockPos getSpawnPoint(ServerPlayerEntity player) { return BlockPos.ORIGIN; }
}
