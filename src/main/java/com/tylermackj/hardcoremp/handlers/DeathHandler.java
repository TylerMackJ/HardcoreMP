package com.tylermackj.hardcoremp.handlers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;

import java.util.List;
import java.util.Random;
import java.util.Vector;

import org.slf4j.Logger;

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
			
			BlockPos spawnPos = newPlayer.getScoreboardTeam().getSpawnPoint(newPlayer);
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
				
				entity.getScoreboardTeam().randomizeSpawnPosForWorld(entity.getWorld());

				PlayerLookup.world((ServerWorld) entity.getWorld()).forEach(player -> {
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
