package com.tylermackj.hardcoremp.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.tylermackj.hardcoremp.HardcoreMPEvents;

import net.minecraft.entity.LivingEntity;

@Mixin(LivingEntity.class)
abstract class AfterHealMixin {
	
	@Inject(method = "heal", at = @At("RETURN"))
	private void afterHeal(float amount, CallbackInfo info) {
		HardcoreMPEvents.AFTER_HEAL.invoker().afterHeal((LivingEntity) (Object) this, amount);
	}
}