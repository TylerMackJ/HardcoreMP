package com.tylermackj.hardcoremp.interfaces;

import java.util.UUID;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ITeamData {
    BlockPos getSpawnPos(World world);
    UUID getAttemptUuid(World world);
    int getAttemptCount(World world);
    long getAttemptStart(World world);
    void nextAttempt(World world);
    void resetAttempts(World world);
}
