package com.example.shadowpanel.mixin;

import com.example.shadowpanel.module.ModuleManager;
import com.example.shadowpanel.module.VelocityModule;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.multiplayer.ClientPacketListener", remap = false)
public class ClientPacketListenerMixin {
    @Inject(method = "handleSetEntityMotion", at = @At("HEAD"), cancellable = true, require = 0)
    private void sp$velocity(Object packet, CallbackInfo ci) {
        try {
            VelocityModule v = ModuleManager.get(VelocityModule.class);
            if (v != null && v.isEnabled()) {
                Minecraft mc = Minecraft.getInstance();
                if (mc.player != null) ci.cancel();
            }
        } catch (Throwable ignored) {}
    }
    @Inject(method = "handleExplosion", at = @At("HEAD"), cancellable = true, require = 0)
    private void sp$velocityExp(Object packet, CallbackInfo ci) {
        try {
            VelocityModule v = ModuleManager.get(VelocityModule.class);
            if (v != null && v.isEnabled()) ci.cancel();
        } catch (Throwable ignored) {}
    }
}
