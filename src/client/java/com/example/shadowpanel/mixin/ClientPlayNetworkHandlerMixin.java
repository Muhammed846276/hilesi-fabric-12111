package com.example.shadowpanel.mixin;

import com.example.shadowpanel.module.ModuleManager;
import com.example.shadowpanel.module.VelocityModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(method = "onEntityVelocityUpdate", at = @At("HEAD"), cancellable = true, require = 0)
    private void sp$velocity(EntityVelocityUpdateS2CPacket packet, CallbackInfo ci) {
        try {
            VelocityModule v = ModuleManager.get(VelocityModule.class);
            MinecraftClient mc = MinecraftClient.getInstance();
            if (v != null && v.isEnabled() && mc.player != null && packet.getEntityId() == mc.player.getId()) ci.cancel();
        } catch (Throwable ignored) {}
    }
    @Inject(method = "onExplosion", at = @At("HEAD"), cancellable = true, require = 0)
    private void sp$velocityExp(ExplosionS2CPacket packet, CallbackInfo ci) {
        try {
            VelocityModule v = ModuleManager.get(VelocityModule.class);
            if (v != null && v.isEnabled()) ci.cancel();
        } catch (Throwable ignored) {}
    }
}
