package com.tylermackj.hardcoremp.types;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

import com.tylermackj.hardcoremp.ComponentRegisterer;
import com.tylermackj.hardcoremp.HardcoreMP;
import com.tylermackj.hardcoremp.Utils;
import com.tylermackj.hardcoremp.interfaces.ITeamData;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class TeamData implements ITeamData, AutoSyncedComponent {
    private final Team provider;

    private static final int RADIUS = 1000000;
    private static final int MAX_SPAWN_HIGHT = 1024;

    private static final String WORLD_ATTEMPT = "worldAttempt";
    private static final String SPAWN_POS = "spawnPos";
    private static final String X = "x";
    private static final String Y = "y";
    private static final String Z = "z";
    private static final String ATTEMPT_UUID = "attemptUuid";
    private static final String ATTEMPT_COUNT = "attemptCount";
    private static final String ATTEMPT_START = "attemptStart";

    private class AttemptData {
        public BlockPos spawnPos = BlockPos.ORIGIN;
        public UUID attemptUuid = UUID.randomUUID();
        public int attemptCount = 0;
        public long attemptStart = 0;

        public AttemptData() {}
        public AttemptData(
            BlockPos spawnPos,
            UUID attemptUuid,
            int attemptCount,
            long attemptStart
        ) {
            this.spawnPos = spawnPos;
            this.attemptUuid = attemptUuid;
            this.attemptCount = attemptCount;
            this.attemptStart = attemptStart;
        }
    }

    private Map<Integer, AttemptData> worldAttempt = new HashMap<>();

    public TeamData(Team provider) { this.provider = provider; }

    private Optional<AttemptData> getWorldAttempt(World world) {
        return Optional.ofNullable(this.worldAttempt.get(world.hashCode()));
    }

    private void setAttempt(World world, int attemptCount) {
        this.worldAttempt.put(world.hashCode(), new AttemptData(
            this.randomizeSpawnPos(world),
            UUID.randomUUID(),
            attemptCount,
            world.getTime()
        ));

        ComponentRegisterer.TEAM_DATA.sync(this.provider);
    }   

    public BlockPos randomizeSpawnPos(World world) {
        Optional<BlockPos> spawnPos = Optional.ofNullable(null);

        do {
            HardcoreMP.LOGGER.info("Generating spawnpoint for team " + this.provider.getName() + " (" + world.asString() + ")");
            BlockPos spawnPoint = Utils.randomBlockPos(RADIUS, MAX_SPAWN_HIGHT);

            TagKey<Biome> oceanTag = TagKey.of(RegistryKeys.BIOME, Identifier.of("c:is_ocean"));

            while (world.getBiome(spawnPoint).isIn(oceanTag)) {
                HardcoreMP.LOGGER.info("Avoiding ocean");
                spawnPoint = Utils.randomBlockPos(RADIUS, MAX_SPAWN_HIGHT);
            }

            HardcoreMP.LOGGER.info("Finding ground");
            spawnPos = BlockPos.findClosest(spawnPoint, 0, MAX_SPAWN_HIGHT, (blockPos) -> {
                return !world.isAir(blockPos.down());
            });
        } while (spawnPos.isEmpty());

        return spawnPos.orElseThrow();
    } 

    @Override
    public void readFromNbt(NbtCompound tag, WrapperLookup registryLookup) {
        Optional<NbtCompound> worldAttemptNbt = tag.getCompound(WORLD_ATTEMPT);

        if (worldAttemptNbt.isPresent()) {
            HardcoreMP.LOGGER.info("Read: " + worldAttemptNbt.toString());

            worldAttemptNbt.orElseThrow().getKeys().forEach(world -> {
                NbtCompound attemptNbt = worldAttemptNbt.orElseThrow().getCompound(world).orElseThrow();
                NbtCompound spawnPosNbt = attemptNbt.getCompound(SPAWN_POS).orElseThrow();

                this.worldAttempt.put(Integer.parseInt(world), new AttemptData(
                    new BlockPos(
                        spawnPosNbt.getInt(X).orElseThrow(),
                        spawnPosNbt.getInt(Y).orElseThrow(),
                        spawnPosNbt.getInt(Z).orElseThrow()
                    ),
                    UUID.fromString(attemptNbt.getString(ATTEMPT_UUID).orElseThrow()),
                    attemptNbt.getInt(ATTEMPT_COUNT).orElseThrow(),
                    attemptNbt.getLong(ATTEMPT_START).orElseThrow()
                ));
            });
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag, WrapperLookup registryLookup) {
        NbtCompound worldAttemptNbt = new NbtCompound();
        this.worldAttempt.keySet().forEach(world -> {
            AttemptData attempt = Optional.ofNullable(this.worldAttempt.get(world)).orElseThrow();

            NbtCompound spawnPosNbt = new NbtCompound();
            spawnPosNbt.putInt(X, attempt.spawnPos.getX());
            spawnPosNbt.putInt(Y, attempt.spawnPos.getY());
            spawnPosNbt.putInt(Z, attempt.spawnPos.getZ());

            NbtCompound attemptNbt = new NbtCompound();
            attemptNbt.put(SPAWN_POS, spawnPosNbt);
            attemptNbt.putString(ATTEMPT_UUID, attempt.attemptUuid.toString());
            attemptNbt.putInt(ATTEMPT_COUNT, attempt.attemptCount);
            attemptNbt.putLong(ATTEMPT_START, attempt.attemptStart);

            worldAttemptNbt.put(world.toString(), attemptNbt);
        });
        HardcoreMP.LOGGER.info("Write: " + worldAttemptNbt.toString());
        tag.put(WORLD_ATTEMPT, worldAttemptNbt);
    }

    @Override
    public BlockPos getSpawnPos(World world) {
        this.worldAttempt.putIfAbsent(world.hashCode(), new AttemptData());
        return this.getWorldAttempt(world).orElse(new AttemptData()).spawnPos;
    }

    @Override
    public UUID getAttemptUuid(World world) {
        this.worldAttempt.putIfAbsent(world.hashCode(), new AttemptData());
        return this.getWorldAttempt(world).orElse(new AttemptData()).attemptUuid;
    }

    @Override
    public int getAttemptCount(World world) {
        this.worldAttempt.putIfAbsent(world.hashCode(), new AttemptData());
        return this.getWorldAttempt(world).orElse(new AttemptData()).attemptCount;
    }

    @Override
    public long getAttemptStart(World world) {
        this.worldAttempt.putIfAbsent(world.hashCode(), new AttemptData());
        return this.getWorldAttempt(world).orElse(new AttemptData()).attemptStart;
    }

    @Override
    public void nextAttempt(World world) {
        this.setAttempt(world, this.getWorldAttempt(world).orElse(new AttemptData()).attemptCount + 1);
    }

    @Override
    public void resetAttempts(World world) {
        this.setAttempt(world, 0);
    }
}
