package com.tylermackj.hardcoremp.types;

import java.util.UUID;

import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

import com.tylermackj.hardcoremp.ComponentRegisterer;
import com.tylermackj.hardcoremp.interfaces.IPlayerData;
import com.tylermackj.hardcoremp.utils.Utils;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;

public class PlayerData implements IPlayerData, AutoSyncedComponent {
    private final Entity provider;

    private static final String NBT_ATTEMPT_UUID = "attemptUuid";

    private UUID attemptUuid = UUID.fromString(Utils.ZERO_UUID_STRING);

    public PlayerData(Entity provider) { this.provider = provider; }

    @Override
    public boolean isRequiredOnClient() {
        return false;
    }

    @Override
    public void readFromNbt(NbtCompound tag, WrapperLookup registryLookup) {
        this.attemptUuid = UUID.fromString(tag.getString(NBT_ATTEMPT_UUID).orElse(Utils.ZERO_UUID_STRING));
    }

    @Override
    public void writeToNbt(NbtCompound tag, WrapperLookup registryLookup) {
        tag.putString(NBT_ATTEMPT_UUID, this.attemptUuid.toString());
    }

    @Override 
    public UUID getAttemptUuid() {
        return this.attemptUuid;
    }

    @Override
    public void setAttemptUuid(UUID attemptUuid) {
        this.attemptUuid = attemptUuid;
        ComponentRegisterer.PLAYER_DATA.sync(this.provider);
    }

    @Override
    public void resetAttemptUuid() {
        this.setAttemptUuid(UUID.fromString(Utils.ZERO_UUID_STRING));
    }
}
