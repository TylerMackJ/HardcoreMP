package com.tylermackj.hardcoremp;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.util.Identifier;

import java.util.Random;
import java.util.Timer;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HardcoreMP implements ModInitializer {
	public static final String MOD_ID = "hardcore-mp";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Random random = new Random();

	public static int attempt = 0;
	public static int ticks = 0;

	public static BlockPos randomBlockPos() {
		int randomSize = 1000000;
		return new BlockPos(
			random.nextInt(-randomSize, randomSize),
			1024,	
			random.nextInt(-randomSize, randomSize)
		);
	}

	@Override
	public void onInitialize() {
		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
			LOGGER.info("Player respawning " + newPlayer.getName() );
			Optional<BlockPos> finalSpawnPoint = Optional.ofNullable(null);
			while (finalSpawnPoint.isEmpty()) {
				LOGGER.info("Generating spawnpoint");
				BlockPos spawnPoint = randomBlockPos();

				TagKey<Biome> oceanTag = TagKey.of(RegistryKeys.BIOME, Identifier.of("c:is_ocean"));

				while (newPlayer.getWorld().getBiome(spawnPoint).isIn(oceanTag)) {
					LOGGER.info("Avoiding ocean");
					spawnPoint = randomBlockPos();
				}

				finalSpawnPoint = BlockPos.findClosest(spawnPoint, 0, 1024, (blockPos) -> {
					LOGGER.info("Finding ground");
					return !newPlayer.getWorld().isAir(blockPos.down());
				});
			}
			newPlayer.requestTeleport(finalSpawnPoint.get().getX(), finalSpawnPoint.get().getY(), finalSpawnPoint.get().getZ());
		});

		ServerLivingEntityEvents.AFTER_DEATH.register((entity, cause) -> {
			if (entity.isPlayer()) {
				LOGGER.info("Player died " + entity.getName() );

				PlayerLookup.world((ServerWorld) entity.getWorld()).forEach(player -> {
					if (player != entity && player.isAlive()) {
						LOGGER.info("Killing " + player.getName());
						player.damage((ServerWorld) player.getWorld(), player.getDamageSources().playerAttack((PlayerEntity) entity), Float.MAX_VALUE);
					}
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