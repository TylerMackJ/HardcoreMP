package com.tylermackj.hardcoremp.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.PlayerListHeaderS2CPacket;
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
            this.sendToAll(new PlayerListHeaderS2CPacket(
				Text.literal("Header"),
				Text.literal("Footer")
			));
            this.customizationUpdateTimer = 0;
        }
    }
}