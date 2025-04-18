package com.tylermackj.hardcoremp.utils;

import java.util.HashSet;
import java.util.Random;

import com.tylermackj.hardcoremp.ComponentRegisterer;
import com.tylermackj.hardcoremp.HardcoreMP;

import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class Utils {
	public static final Random random = new Random();
    public static final String ZERO_UUID_STRING = "00000000-0000-0000-0000-000000000000";

	private static HashSet<String> lockedTeams = new HashSet<>();

	public static BlockPos randomBlockPos(int radius, int y) {
		return new BlockPos(
			random.nextInt(-radius, radius),
			y,	
			random.nextInt(-radius, radius)
		);
	}
	
	public static void checkAttemptUuid(ServerPlayerEntity player) {
		if (player.getScoreboardTeam() == null) {
			HardcoreMP.LOGGER.info("Player " + player.getName() + " is not on a team");
			return;
		}

		HardcoreMP.LOGGER.info("Comparing teams attempt UUID (" + player.getScoreboardTeam().getComponent(ComponentRegisterer.TEAM_DATA).getAttemptUuid().toString() + ") to players attempt UUID (" + player.getComponent(ComponentRegisterer.PLAYER_DATA).getAttemptUuid() + ")");
		if (!player.getScoreboardTeam().getComponent(ComponentRegisterer.TEAM_DATA).getAttemptUuid().equals(player.getComponent(ComponentRegisterer.PLAYER_DATA).getAttemptUuid())) {
			HardcoreMP.LOGGER.info("Player " + player.getName() + " is not on current attempt");
			lockTeam(player.getScoreboardTeam());
			player.requestTeleport(0, -1024, 0);	
		}
	}

	public static void lockTeam(Team team) {
		HardcoreMP.LOGGER.info("Locking team: " + team.getName());
		lockedTeams.add(team.getName());
	}

	public static void unlockTeam(Team team) {
		HardcoreMP.LOGGER.info("Unlocking team: " + team.getName());
		lockedTeams.remove(team.getName());
	}

	public static Boolean teamLocked(Team team) {
        Boolean locked = lockedTeams.contains(team.getName());
		HardcoreMP.LOGGER.info("Checking lock for team " + team.getName() + ": " + locked);
		return locked;
	}
}
