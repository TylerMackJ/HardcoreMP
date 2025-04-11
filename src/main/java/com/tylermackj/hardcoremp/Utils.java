package com.tylermackj.hardcoremp;

import java.util.Random;

import net.minecraft.util.math.BlockPos;

public class Utils {
	public static final Random random = new Random();

	public static BlockPos randomBlockPos(int radius) {
		return new BlockPos(
			random.nextInt(-radius, radius),
			1024,	
			random.nextInt(-radius, radius)
		);
	}
}
