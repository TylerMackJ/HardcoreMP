package com.tylermackj.hardcoremp.handlers;

import org.slf4j.Logger;

import com.tylermackj.hardcoremp.HardcoreMP;
import com.tylermackj.hardcoremp.Utils;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.server.network.ServerPlayerEntity;

public class JoinHandler {

    public static final Logger LOGGER = HardcoreMP.LOGGER;

    public static void registerEvents() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity.isPlayer()) {
                LOGGER.info("Player joined " + entity.getName() );
                Utils.checkAttempt((ServerPlayerEntity) entity);
            }
        });
    }

}
