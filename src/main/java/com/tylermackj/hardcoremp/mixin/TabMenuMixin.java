package com.tylermackj.hardcoremp.mixin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.tylermackj.hardcoremp.HardcoreMP;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.PlayerListHeaderS2CPacket;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.PlayerManager;
import net.minecraft.text.Text;

@Mixin(PlayerManager.class)
abstract class TabMenuMixin {
	
    public int customizationUpdateTimer;

    @Shadow
    public abstract void sendToAll(Packet<?> packet);

    @Inject(at = @At("HEAD"), method = "updatePlayerLatency")
    public void updatePlayerLatency(CallbackInfo callbackInfo) {
        if ( ++this.customizationUpdateTimer > 20 ) {
            Map<Team, List<String>> teams = new HashMap<>();

            if (HardcoreMP.minecraftServer.isEmpty()) {
                return;
            }
			PlayerLookup.all(HardcoreMP.minecraftServer.get()).forEach(serverPlayerEntity -> {
                if (serverPlayerEntity.getScoreboardTeam() != null) {
                    teams.putIfAbsent(serverPlayerEntity.getScoreboardTeam(), new Vector<>());

                    teams.get(serverPlayerEntity.getScoreboardTeam()).add(serverPlayerEntity.getName().getLiteralString());
                }
            });

            StringBuffer footer = new StringBuffer();
            for (Team team : teams.keySet()) {
                footer.append(team.getDisplayName().getLiteralString() + "\n");
                for (String name : teams.get(team)) {
                    footer.append("  " + name + "\n");
                }
            }


            this.sendToAll(new PlayerListHeaderS2CPacket(
				Text.literal("Header"),
				Text.literal(footer.toString())
			));
            this.customizationUpdateTimer = 0;
        }
    }
}