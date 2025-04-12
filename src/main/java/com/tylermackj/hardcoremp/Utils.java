package com.tylermackj.hardcoremp;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import org.slf4j.Logger;

import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class Utils {
    public static final Logger LOGGER = HardcoreMP.LOGGER;
	public static final Random random = new Random();

	private static HashSet<String> lockedTeams = new HashSet<>();

	public static BlockPos randomBlockPos(int radius) {
		return new BlockPos(
			random.nextInt(-radius, radius),
			1024,	
			random.nextInt(-radius, radius)
		);
	}
	
	public static void checkAttempt(ServerPlayerEntity player) {
		if (player.getScoreboardTeam() == null) {
			LOGGER.info("Player " + player.getName() + " is not on a team");
			return;
		}

		LOGGER.info("Checking if player " + player.getName() + " is on current attempt");
		if (player.getScoreboardTeam().getAttemptUuid() != player.getAttempt()) {
			LOGGER.info("Player " + player.getName() + " is not on current attempt");
			lockTeam(player.getScoreboardTeam());
			player.requestTeleport(0, -1024, 0);	
		}
	}

	public static void lockTeam(Team team) {
		LOGGER.info("Locking team: " + team.getName());
		lockedTeams.add(team.getName());
	}

	public static void unlockTeam(Team team) {
		LOGGER.info("Unlocking team: " + team.getName());
		lockedTeams.remove(team.getName());
	}

	public static Boolean teamLocked(Team team) {
        Boolean locked = lockedTeams.contains(team.getName());
		LOGGER.info("Checking lock for team " + team.getName() + ": " + locked);
		return locked;
	}
}
