package com.tylermackj.hardcoremp;

import java.util.HashMap;
import java.util.UUID;

import com.mojang.datafixers.types.Type;
import com.tylermackj.hardcoremp.types.PlayerData;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;
import net.minecraft.world.World;

public class StateSaverAndLoader extends PersistentState {
    public HashMap<UUID, PlayerData> players = new HashMap<>();

    public static StateSaverAndLoader createFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        StateSaverAndLoader state = new StateSaverAndLoader();
        return state;
    }

    public static StateSaverAndLoader createNew() {
        StateSaverAndLoader state = new StateSaverAndLoader();
        return state;
    }

    private static final PersistentStateType<StateSaverAndLoader> type = new PersistentStateType<>(
        HardcoreMP.MOD_ID, 
        StateSaverAndLoader::createNew,
        StateSaverAndLoader::createFromNbt,
        null
    );

    public static StateSaverAndLoader getServerState(MinecraftServer server) {
        ServerWorld serverWorld = server.getWorld(World.OVERWORLD);
        assert serverWorld != null;

        StateSaverAndLoader state = serverWorld.getPersistentStateManager().getOrCreate(type);

        state.markDirty();
        
        return state;
    }

    public static PlayerData getPlayerState(LivingEntity player) {
        StateSaverAndLoader serverState = getServerState(player.getWorld().getServer());

        PlayerData playerState = serverState.players.computeIfAbsent(player.getUuid(), uuid -> new PlayerData());

        player.setAttempt(playerState.attempt);
    }
}
