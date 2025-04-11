package com.tylermackj.hardcoremp.mixin;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;

import com.tylermackj.hardcoremp.HardcoreMP;
import com.tylermackj.hardcoremp.Utils;
import com.tylermackj.hardcoremp.injects.TeamExtension;

import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

@Mixin(Team.class)
public class TeamExtensionMixin implements TeamExtension {
    private static final Logger LOGGER = HardcoreMP.LOGGER;
    private static final int RADIUS = 1000000;

    private Map<World, Optional<BlockPos>> worldToSpawnPos = new HashMap<>();        

    @Override
    public void randomizeSpawnPosForWorld(World world) {
        this.worldToSpawnPos.putIfAbsent(world, Optional.ofNullable(null));

        if (this.worldToSpawnPos.get(world).isPresent()) {
            LOGGER.debug("Spawnpoint for team " + ((Team) (Object) this).getName() + " (" + world.asString() + ") has not been used yet");
            return;
        }

        while (this.worldToSpawnPos.get(world).isEmpty()) {
            LOGGER.info("Generating spawnpoint for team " + ((Team) (Object) this).getName() + " (" + world.asString() + ")");
            BlockPos spawnPoint = Utils.randomBlockPos(RADIUS);

            TagKey<Biome> oceanTag = TagKey.of(RegistryKeys.BIOME, Identifier.of("c:is_ocean"));

            while (world.getBiome(spawnPoint).isIn(oceanTag)) {
                LOGGER.debug("Avoiding ocean");
                spawnPoint = Utils.randomBlockPos(RADIUS);
            }

            LOGGER.debug("Finding ground");
            this.worldToSpawnPos.put(world, BlockPos.findClosest(spawnPoint, 0, 1024, (blockPos) -> {
                return !world.isAir(blockPos.down());
            }));
        }
    } 

    @Override
    public BlockPos getSpawnPoint(World world) {
        Optional<BlockPos> spawnPos = this.worldToSpawnPos.get(world);
        this.worldToSpawnPos.put(world, Optional.ofNullable(null));

        if (spawnPos.isEmpty()) {
            LOGGER.error("No spawnpoint for team " + ((Team) (Object) this).getName() + " (" + world.asString() + ")");
        }
        return spawnPos.get();
    }
}
