package com.tylermackj.hardcoremp;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HardcoreMP implements ModInitializer {
	public static final String MOD_ID = "hardcore-mp";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ServerLivingEntityEvents.AFTER_DEATH.register((entity, cause) -> {
			if (entity.isPlayer()) {
				LOGGER.info("Player died " + entity.getName() );

				PlayerLookup.world((ServerWorld) entity.getWorld()).forEach(player -> {
					if (player != entity && player.isAlive()) {
						LOGGER.info("Killing " + player.getName());
						player.damage((ServerWorld) player.getWorld(), player.getDamageSources().playerAttack((PlayerEntity) entity), Float.MAX_VALUE);
					}

					player.setSpawnPoint(player.Respawn, );
				});
			}
		});

		ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, baseDamageTaken, damageTaken, blocked) -> {
			if (entity.isPlayer()) {
				LOGGER.info("Player " + entity.getName() + " took " + damageTaken + " damage");

				PlayerLookup.world((ServerWorld) entity.getWorld()).forEach(player -> {
					if (player != entity && player.isAlive()) {
						LOGGER.info("Setting " + player.getName() + " health to " + entity.getHealth());
						player.setHealth(entity.getHealth());
					}
				});
			}
		});

		HardcoreMPEvents.AFTER_HEAL.register((entity, amount) -> {
			if (entity.isPlayer()) {
				LOGGER.info("Player " + entity.getName() + " healed for " + amount);

				PlayerLookup.world((ServerWorld) entity.getWorld()).forEach(player -> {
					if (player != entity && player.isAlive()) {
						LOGGER.info("Setting " + player.getName() + " health to " + entity.getHealth());
						player.setHealth(entity.getHealth());
					}
				});
			}
		});
	}
}