package com.example.shadowpanel.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import java.util.ArrayList;
import java.util.List;

public class ShadowHudRenderer {
    public static void render(MinecraftClient mc, DrawContext ctx) {
        try {
            if (mc == null || mc.player == null || ctx == null) return;
            HudModule hud = ModuleManager.get(HudModule.class);
            InfoHudModule info = ModuleManager.get(InfoHudModule.class);
            List<String> lines = new ArrayList<>();
            if (info != null && info.isEnabled()) {
                if (info.isFps()) lines.add("FPS: " + mc.getCurrentFps());
                if (info.isCoords()) lines.add(String.format("XYZ: %.1f %.1f %.1f", mc.player.getX(), mc.player.getY(), mc.player.getZ()));
            }
            if (hud != null && hud.isEnabled()) {
                for (Module m : ModuleManager.getModules()) if (m.isEnabled() && !(m instanceof HudModule) && !(m instanceof InfoHudModule)) lines.add(m.getName());
            }
            int y = 4;
            for (String s : lines) { ctx.drawTextWithShadow(mc.textRenderer, s, 4, y, 0xFFAA55FF); y += 10; }
        } catch (Throwable ignored) {}
    }
}
