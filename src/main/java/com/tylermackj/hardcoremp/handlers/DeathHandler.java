package com.tylermackj.hardcoremp.handlers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;

import java.util.Random;

import org.slf4j.Logger;

import com.tylermackj.hardcoremp.ComponentRegisterer;
import com.tylermackj.hardcoremp.HardcoreMP;
import com.tylermackj.hardcoremp.Utils;

public class DeathHandler {

    public static final Logger LOGGER = HardcoreMP.LOGGER;

	public static final Random random = new Random();

    public static void registerEvents() {
		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
			LOGGER.info("Player respawning " + newPlayer.getName() );

			if (newPlayer.getScoreboardTeam() == null) {
				return;
			}
			
			Utils.unlockTeam(newPlayer.getScoreboardTeam());

			// Get spawn position from team
        	BlockPos spawnPos = newPlayer.getScoreboardTeam().getComponent(ComponentRegisterer.TEAM_DATA).getSpawnPos();
			// Spawn position could be not set if an attempt has never been started
			if (spawnPos == BlockPos.ORIGIN) {
				// Start new attempt and get spawn position again
        		newPlayer.getScoreboardTeam().getComponent(ComponentRegisterer.TEAM_DATA).nextAttempt(newPlayer.getWorld());
        		spawnPos = newPlayer.getScoreboardTeam().getComponent(ComponentRegisterer.TEAM_DATA).getSpawnPos();
			}
			// Set player attempt uuid to teams attempt uuid now that player has joined attempt
			newPlayer.getComponent(ComponentRegisterer.PLAYER_DATA).setAttemptUuid(
				newPlayer.getScoreboardTeam().getComponent(ComponentRegisterer.TEAM_DATA).getAttemptUuid()
			);
			// Teleport player to start of attempt
			newPlayer.requestTeleport(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
		});

		ServerLivingEntityEvents.AFTER_DEATH.register((entity, cause) -> {
			if (entity.isPlayer()) {
				LOGGER.debug("Player died " + entity.getName());

				if (entity.getScoreboardTeam() == null) {
					LOGGER.info("Player not on team");
					return;
				}

				if (Utils.teamLocked(entity.getScoreboardTeam())) {
					return;
				}

				Utils.lockTeam(entity.getScoreboardTeam());
				
				long attemptLengthTicks = entity.getWorld().getTime() - entity.getScoreboardTeam().getComponent(ComponentRegisterer.TEAM_DATA).getAttemptStart();
				String attemptLengthTS = String.format(
					"%02d:%02d:%02d", 
					(attemptLengthTicks / 20 / 60 / 60) % 24,
					(attemptLengthTicks / 20 / 60 ) % 60,
					(attemptLengthTicks / 20) % 60
				);

        		entity.getScoreboardTeam().getComponent(ComponentRegisterer.TEAM_DATA).nextAttempt(entity.getWorld());

				PlayerLookup.world((ServerWorld) entity.getWorld()).forEach(player -> {
					player.sendMessage(Text.literal("Team " + entity.getScoreboardTeam().getName() + " has died after " + attemptLengthTS + " on attempt " + entity.getScoreboardTeam().getComponent(ComponentRegisterer.TEAM_DATA).getAttemptCount()));
					if (
						player != entity &&
						 player.isAlive() &&
						player.getScoreboardTeam() == entity.getScoreboardTeam() 
					) {
						LOGGER.debug("Killing " + player.getName());
						player.damage((ServerWorld) player.getWorld(), player.getDamageSources().playerAttack((PlayerEntity) entity), Float.MAX_VALUE);
					}
				});
			}
		});
    }
}
