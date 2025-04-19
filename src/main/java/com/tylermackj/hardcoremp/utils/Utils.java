package com.tylermackj.hardcoremp.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import com.tylermackj.hardcoremp.ComponentRegisterer;
import com.tylermackj.hardcoremp.HardcoreMP;

import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class Utils {
	public static final Random random = new Random();
    public static final String ZERO_UUID_STRING = "00000000-0000-0000-0000-000000000000";

	private static Map<String, HashSet<Entity>> lockedTeams = new HashMap<>();

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
			lockTeam(player);
			player.requestTeleport(0, -1024, 0);	
		}
	}

	public static void lockTeam(Entity player) {
		HardcoreMP.LOGGER.info("Locking team: " + player.getScoreboardTeam().getName());
		lockedTeams.putIfAbsent(player.getScoreboardTeam().getName(), new HashSet<Entity>());
		lockedTeams.computeIfPresent(player.getScoreboardTeam().getName(), (name, players) -> { players.add(player); return players; });
	}

	public static void unlockTeam(Entity player) {
		HardcoreMP.LOGGER.info("Unlocking team: " + player.getScoreboardTeam().getName());
		Optional<HashSet<Entity>> newPlayers = Optional.ofNullable(lockedTeams.computeIfPresent(player.getScoreboardTeam().getName(), (name, players) -> { players.remove(player); return players; }));
		if (newPlayers.isPresent() && newPlayers.get().isEmpty()) {
			lockedTeams.remove(player.getScoreboardTeam().getName());
		}
	}

	public static Boolean teamLocked(Team team) {
        Boolean locked = lockedTeams.containsKey(team.getName());
		HardcoreMP.LOGGER.info("Checking lock for team " + team.getName() + ": " + locked);
		return locked;
	}
}
