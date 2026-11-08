package com.example.shadowpanel.mixin;

import com.example.shadowpanel.module.ShadowHudRenderer;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.gui.Gui", remap = false)
public class GuiMixin {
    @Inject(method = "render", at = @At("TAIL"), require = 0)
    private void sp$hud(CallbackInfo ci) {
        try { ShadowHudRenderer.render26(Minecraft.getInstance()); } catch (Throwable ignored) {}
    }
    @Inject(method = "render", at = @At("TAIL"), require = 0)
    private void sp$hud2(Object a, Object b, CallbackInfo ci) {
        try { if (a != null) ShadowHudRenderer.render26(Minecraft.getInstance(), a); } catch (Throwable ignored) {}
    }
}
