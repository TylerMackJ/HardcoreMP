package com.tylermackj.hardcoremp.injects;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface TeamExtension {
    default void randomizeSpawnPosForWorld(World world) {}
    default BlockPos getSpawnPoint(World world) { return BlockPos.ORIGIN; }
}
