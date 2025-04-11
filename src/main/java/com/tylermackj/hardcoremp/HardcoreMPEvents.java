package com.tylermackj.hardcoremp;

import net.minecraft.entity.LivingEntity;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class HardcoreMPEvents {
    public static final Event<AfterHeal> AFTER_HEAL = EventFactory.createArrayBacked(AfterHeal.class, callbacks -> (entity, amount) -> {
        for (AfterHeal callback : callbacks) {
            callback.afterHeal(entity, amount);
        }
    });

    @FunctionalInterface
    public interface AfterHeal {
        void afterHeal(LivingEntity entity, float amount);
    }
}
