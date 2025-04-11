package com.tylermackj.hardcoremp;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tylermackj.hardcoremp.handlers.DeathHandler;
import com.tylermackj.hardcoremp.handlers.HealthHandler;

public class HardcoreMP implements ModInitializer {
	public static final String MOD_ID = "hardcore-mp";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		HealthHandler.registerEvents();
		DeathHandler.registerEvents();
	}
}