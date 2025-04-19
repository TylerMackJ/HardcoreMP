package com.tylermackj.hardcoremp;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tylermackj.hardcoremp.handlers.CommandHandler;
import com.tylermackj.hardcoremp.handlers.DeathHandler;
import com.tylermackj.hardcoremp.handlers.HealthHandler;
import com.tylermackj.hardcoremp.handlers.ConnectionHandler;
import com.tylermackj.hardcoremp.utils.SpawnPosProvider;

public class HardcoreMP implements ModInitializer {
	public static final String MOD_ID = "hardcore-mp";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Optional<MinecraftServer> minecraftServer = Optional.ofNullable(null);

	@Override
	public void onInitialize() {
		ServerLifecycleEvents.SERVER_STARTED.register(minecraftServer -> {
			HardcoreMP.minecraftServer = Optional.of(minecraftServer);	
			SpawnPosProvider.INSTANCE.init(minecraftServer.getOverworld());
		});
		CommandHandler.registerCommands();
		ConnectionHandler.registerEvents();
		HealthHandler.registerEvents();
		DeathHandler.registerEvents();
	}
}