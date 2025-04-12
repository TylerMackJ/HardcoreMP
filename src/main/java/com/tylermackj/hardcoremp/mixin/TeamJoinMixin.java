package com.tylermackj.hardcoremp.mixin;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.tylermackj.hardcoremp.HardcoreMP;
import com.tylermackj.hardcoremp.Utils;

import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(Scoreboard.class)
public class TeamJoinMixin {
    private static final Logger LOGGER = HardcoreMP.LOGGER;

    @Inject(method = "addScoreHolderToTeam", at = @At("RETURN"), cancellable = true) 
	private void afterTeamJoin(String scoreHolderName, Team team, CallbackInfoReturnable<Boolean> info) {
        LOGGER.info("Player joining team");
        if (HardcoreMP.minecraftServer.isEmpty()) {
            LOGGER.info("No MinecraftServer");
            return;
        }

        Optional<ServerPlayerEntity> player = Optional.ofNullable(HardcoreMP.minecraftServer.get().getPlayerManager().getPlayer(scoreHolderName));

        if (player.isEmpty()) {
            LOGGER.info("No player found with name: " + scoreHolderName);
            return;
        }

        player.get().setAttempt(UUID.randomUUID());
        Utils.checkAttempt(player.get());
	}
}
