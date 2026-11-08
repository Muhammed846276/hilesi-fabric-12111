package com.example.shadowpanel.module;

import net.minecraft.client.Minecraft;

/**
 * XRay (client-side, deneysel). Sunucu anti-xray (Orebfuscator/Paper) aktifse
 * sunucu gercek cevher verisini gondermez; istemci bunu asamaz.
 */
public class XRayModule extends Module {
    private boolean caveMode = false;
    public XRayModule() { super("XRay"); }
    public boolean isCaveMode() { return caveMode; }
    public void toggleCaveMode() { caveMode = !caveMode; }

    private void reload(Minecraft c) {
        if (c == null) return;
        try {
            Object lr = c.levelRenderer;
            if (lr == null) return;
            for (String name : new String[]{"allChanged", "needsUpdate", "setAllDirty", "setNeedsUpdate"}) {
                try { lr.getClass().getMethod(name).invoke(lr); return; } catch (Throwable ignored) {}
            }
        } catch (Throwable ignored) {}
    }

    @Override protected void onEnable() { reload(Minecraft.getInstance()); }
    @Override protected void onDisable() { reload(Minecraft.getInstance()); }
}
