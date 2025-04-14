package com.tylermackj.hardcoremp;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public enum SpawnPosProvider {
    INSTANCE;

    private static final int RADIUS = 1000000;
    private static final int CHUNK_GEN_RADIUS = 4;
    private static final int MAX_SPAWN_HIGHT = 1024;
    private static final int QUEUE_SIZE = 1;

    private Queue<BlockPos> spawnPosQueue = new ConcurrentLinkedQueue<>();
    private Optional<World> world = Optional.ofNullable(null);
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public void init(World world) {
        this.world = Optional.of(world);
        fillQueue();
    }

    public void addSpawnPosToQueue(Boolean preGenerate) {
        if (world.isEmpty()) {
            throw new IllegalStateException();
        }

        HardcoreMP.LOGGER.info("Generating spawn");
        Optional<BlockPos> spawnPos = Optional.ofNullable(null);

        do {
            BlockPos spawnPoint = Utils.randomBlockPos(RADIUS, MAX_SPAWN_HIGHT);

            TagKey<Biome> oceanTag = TagKey.of(RegistryKeys.BIOME, Identifier.of("c:is_ocean"));

            while (world.get().getBiome(spawnPoint).isIn(oceanTag)) {
                HardcoreMP.LOGGER.info("Avoiding ocean");
                spawnPoint = Utils.randomBlockPos(RADIUS, MAX_SPAWN_HIGHT);
            }

            HardcoreMP.LOGGER.info("Finding ground");
            spawnPos = BlockPos.findClosest(spawnPoint, 0, MAX_SPAWN_HIGHT, (blockPos) -> {
                return !world.get().isAir(blockPos.down());
            });
        } while (spawnPos.isEmpty());

        spawnPosQueue.add(spawnPos.orElseThrow());

        if (preGenerate) {
            HardcoreMP.LOGGER.info("Generating chunks");

            for(int x = spawnPos.get().getX() - (16 * CHUNK_GEN_RADIUS); x <= spawnPos.get().getX() + (16 * CHUNK_GEN_RADIUS); x += 16) {
                for(int z = spawnPos.get().getZ() - (16 * CHUNK_GEN_RADIUS); z <= spawnPos.get().getZ() + (16 * CHUNK_GEN_RADIUS); z += 16) {
                    world.get().getChunk(new BlockPos(x, 0, z));
                }
            }
        }
    }

    public void fillQueue() {
        executor.submit(new Runnable() {
            public void run() {
                for (int i = spawnPosQueue.size(); i < QUEUE_SIZE; i++) {
                    addSpawnPosToQueue(true);
                }
            }
        });
    }

    public BlockPos getSpawnPos() {
        if (spawnPosQueue.isEmpty()) {
            addSpawnPosToQueue(false);
        }
        BlockPos spawnPos = spawnPosQueue.remove();
        fillQueue();
        return spawnPos;
    }
}
