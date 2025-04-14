package com.tylermackj.hardcoremp.interfaces;

import java.util.UUID;

import org.ladysnake.cca.api.v3.component.ComponentV3;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ITeamData extends ComponentV3 {
    BlockPos getSpawnPos(World world);
    UUID getAttemptUuid(World world);
    int getAttemptCount(World world);
    long getAttemptStart(World world);
    void nextAttempt(World world);
    void resetAttempts(World world);
}
