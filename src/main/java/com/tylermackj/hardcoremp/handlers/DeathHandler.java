package com.tylermackj.hardcoremp.handlers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;

import java.util.Random;

import org.slf4j.Logger;

import com.tylermackj.hardcoremp.HardcoreMP;

public class DeathHandler {

    public static final Logger LOGGER = HardcoreMP.LOGGER;

	public static final Random random = new Random();

    public static void registerEvents() {
		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
			LOGGER.debug("Player respawning " + newPlayer.getName() );
			
			BlockPos spawnPos = newPlayer.getScoreboardTeam().getSpawnPoint(newPlayer.getWorld());
			newPlayer.requestTeleport(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
		});

		ServerLivingEntityEvents.AFTER_DEATH.register((entity, cause) -> {
			if (entity.isPlayer()) {
				LOGGER.debug("Player died " + entity.getName());

				entity.getScoreboardTeam().randomizeSpawnPosForWorld(entity.getWorld());

				PlayerLookup.world((ServerWorld) entity.getWorld()).forEach(player -> {
					if (player != entity && player.isAlive()) {
						LOGGER.debug("Killing " + player.getName());
						player.damage((ServerWorld) player.getWorld(), player.getDamageSources().playerAttack((PlayerEntity) entity), Float.MAX_VALUE);
					}
				});
			}
		});
    }
}
