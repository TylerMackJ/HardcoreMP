package com.tylermackj.hardcoremp.handlers;

import net.minecraft.server.world.ServerWorld;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;

import org.slf4j.Logger;

import com.tylermackj.hardcoremp.HardcoreMP;
import com.tylermackj.hardcoremp.HardcoreMPEvents;
import com.tylermackj.hardcoremp.Utils;

public class HealthHandler {

    public static final Logger LOGGER = HardcoreMP.LOGGER;

    public static void registerEvents() {
		ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, baseDamageTaken, damageTaken, blocked) -> {
			if (entity.isPlayer()) {
				LOGGER.debug("Player " + entity.getName() + " took " + damageTaken + " damage");

				if (entity.getScoreboardTeam() == null) {
					return;
				}

				if (Utils.teamLocked(entity.getScoreboardTeam())) {
					return;
				}

				PlayerLookup.world((ServerWorld) entity.getWorld()).forEach(player -> {
					if (
						player != entity && 
						player.isAlive() && 
						player.getScoreboardTeam() == entity.getScoreboardTeam()
					) {
						LOGGER.debug("Setting " + player.getName() + " health to " + entity.getHealth());
						player.setHealth(entity.getHealth());
					}
				});
			}
		});

		HardcoreMPEvents.AFTER_HEAL.register((entity, amount) -> {
			if (entity.isPlayer()) {
				LOGGER.debug("Player " + entity.getName() + " healed for " + amount);

				if (entity.getScoreboardTeam() == null) {
					return;
				}

				if (Utils.teamLocked(entity.getScoreboardTeam())) {
					return;
				}

				PlayerLookup.world((ServerWorld) entity.getWorld()).forEach(player -> {
					if (
						player != entity && 
						player.isAlive() && 
						player.getScoreboardTeam() == entity.getScoreboardTeam()
					) {
						LOGGER.debug("Setting " + player.getName() + " health to " + entity.getHealth());
						player.setHealth(entity.getHealth());
					}
				});
			}
		});
    }
}
