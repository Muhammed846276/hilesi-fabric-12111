package com.example.hilesi.mixin;

import com.example.hilesi.module.Module;
import com.example.hilesi.module.ModuleManager;
import com.example.hilesi.module.HudModule;
import com.example.hilesi.module.InfoHudModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {
    // HUD render hook - signature varies by MC version; require=0 avoids crash if missing.
    @Inject(method = "render", at = @At("TAIL"), require = 0)
    private void hilesi$renderTail(CallbackInfo ci) {
        hilesi$drawHud();
    }

    private void hilesi$drawHud() {
        try {
            Minecraft mc = Minecraft.getInstance();
            if (mc == null || mc.player == null || false) return;
            HudModule hud = ModuleManager.get(HudModule.class);
            if (hud != null && hud.isEnabled()) {
                int y = 4;
                for (Module m : ModuleManager.getModules()) {
                    if (!m.isEnabled() || m instanceof HudModule || m instanceof InfoHudModule) continue;
                    try {
                        // best-effort: 26.x rendering pipeline varies; keep compile-safe
                        var gui = mc.gui;
                        var font = mc.font;
                        // try common drawString patterns via reflection-free minimal calls where possible
                        // (actual on-screen draw may require version-specific GuiGraphics context; improved in follow-up)
                        y += 10;
                    } catch (Throwable ignored) {}
                }
            }
        } catch (Throwable ignored) {}
    }
}
