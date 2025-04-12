package com.tylermackj.hardcoremp.mixin;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;

import com.tylermackj.hardcoremp.HardcoreMP;
import com.tylermackj.hardcoremp.Utils;
import com.tylermackj.hardcoremp.injects.TeamExtension;

import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

@Mixin(Team.class)
public class TeamExtensionMixin implements TeamExtension {
    private static final Logger LOGGER = HardcoreMP.LOGGER;
    private static final int RADIUS = 1000000;

    private Map<World, Optional<BlockPos>> worldToSpawnPos = new HashMap<>();
    private int attempt = 0;
    private UUID attemptUuid = UUID.randomUUID();
    private long attemptStart = 0;
    
    @Override
    public int getAttempt() { return this.attempt; }

    @Override
    public UUID getAttemptUuid() { return this.attemptUuid; }

    @Override
    public void randomizeSpawnPosForWorld(World world) {
        this.attempt++;
        this.attemptUuid = UUID.randomUUID();
        this.attemptStart = world.getTime();

        this.worldToSpawnPos.putIfAbsent(world, Optional.ofNullable(null));

        do {
            LOGGER.info("Generating spawnpoint for team " + ((Team) (Object) this).getName() + " (" + world.asString() + ")");
            BlockPos spawnPoint = Utils.randomBlockPos(RADIUS);

            TagKey<Biome> oceanTag = TagKey.of(RegistryKeys.BIOME, Identifier.of("c:is_ocean"));

            while (world.getBiome(spawnPoint).isIn(oceanTag)) {
                LOGGER.info("Avoiding ocean");
                spawnPoint = Utils.randomBlockPos(RADIUS);
            }

            LOGGER.info("Finding ground");
            this.worldToSpawnPos.put(world, BlockPos.findClosest(spawnPoint, 0, 1024, (blockPos) -> {
                return !world.isAir(blockPos.down());
            }));
        } while (this.worldToSpawnPos.get(world).isEmpty());
    } 

    @Override
    public BlockPos getSpawnPoint(ServerPlayerEntity player) {
        LOGGER.info("Getting spawnPoint");

        this.worldToSpawnPos.putIfAbsent(player.getWorld(), Optional.ofNullable(null));
        Optional<BlockPos> spawnPos = this.worldToSpawnPos.get(player.getWorld());

        if (spawnPos.isEmpty()) {
            LOGGER.error("No spawnpoint for team " + ((Team) (Object) this).getName() + " (" + player.getWorld().asString() + ") generating new one");
            randomizeSpawnPosForWorld(player.getWorld());
            return getSpawnPoint(player);
        }

        player.setAttempt(this.attemptUuid);
        return spawnPos.get();
    }
}
