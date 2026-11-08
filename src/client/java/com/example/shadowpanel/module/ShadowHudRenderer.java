package com.example.shadowpanel.module;

import net.minecraft.client.Minecraft;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ShadowHudRenderer {
    public static void render26(Minecraft mc) { render26(mc, null); }
    public static void render26(Minecraft mc, Object graphics) {
        try {
            if (mc == null || mc.player == null) return;
            HudModule hud = ModuleManager.get(HudModule.class);
            InfoHudModule info = ModuleManager.get(InfoHudModule.class);
            int y = 4;
            List<String> lines = new ArrayList<>();
            if (info != null && info.isEnabled()) {
                if (info.isFps()) lines.add("FPS: " + mc.getFps());
                if (info.isCoords()) lines.add(String.format("XYZ: %.1f %.1f %.1f", mc.player.getX(), mc.player.getY(), mc.player.getZ()));
            }
            if (hud != null && hud.isEnabled()) {
                for (Module m : ModuleManager.getModules()) if (m.isEnabled() && !(m instanceof HudModule) && !(m instanceof InfoHudModule)) lines.add(m.getName());
            }
            if (lines.isEmpty()) return;
            Object ctx = graphics != null ? graphics : tryGraphics(mc);
            for (String s : lines) {
                drawText(ctx, mc, s, 4, y, 0xFFAA55FF);
                y += 10;
            }
        } catch (Throwable ignored) {}
    }
    private static Object tryGraphics(Minecraft mc) {
        try { // best effort - 26.x rendering varies
            return null;
        } catch (Throwable t) { return null; }
    }
    private static void drawText(Object ctx, Minecraft mc, String s, int x, int y, int color) {
        try {
            Object font = mc.font;
            if (ctx == null || font == null) return;
            // try GuiGraphicsExtractor/GuiGraphics drawString/text variants reflectively
            for (Method m : ctx.getClass().getMethods()) {
                if (m.getName().equals("drawString") || m.getName().equals("text")) {
                    Class<?>[] p = m.getParameterTypes();
                    try {
                        if (p.length >= 4 && p[1] == String.class && p[2] == int.class && p[3] == int.class) {
                            // (Font, String, x, y, color, ...)
                            Object[] args = new Object[p.length];
                            args[0]=font; args[1]=s; args[2]=x; args[3]=y; if (p.length>4) args[4]=color;
                            for(int i=5;i<p.length;i++){ if(p[i]==boolean.class) args[i]=true; else if(p[i]==int.class) args[i]=0; else args[i]=null; }
                            m.invoke(ctx, args); return;
                        }
                    } catch (Throwable ignored) {}
                }
            }
        } catch (Throwable ignored) {}
    }
}
