package com.tylermackj.hardcoremp.mixin;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;

import com.tylermackj.hardcoremp.injects.PlayerExtension;

import net.minecraft.entity.LivingEntity;

@Mixin(LivingEntity.class)
public class PlayerExtensionMixin implements PlayerExtension {
    private UUID attempt = UUID.randomUUID();

    @Override
    public void setAttempt(UUID attempt) {
        this.attempt = attempt;
    }

    @Override
    public UUID getAttempt() { return this.attempt; }
}
