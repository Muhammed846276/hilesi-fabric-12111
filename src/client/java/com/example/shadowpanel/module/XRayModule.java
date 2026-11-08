package com.example.shadowpanel.module;

import net.minecraft.client.Minecraft;

public class XRayModule extends Module {
    private boolean caveMode = false;
    public XRayModule() { super("XRay"); }
    public boolean isCaveMode() { return caveMode; }
    public void toggleCaveMode() { caveMode = !caveMode; }
    private void reload(Minecraft c){
        try { if (c != null && c.levelRenderer != null) c.levelRenderer.allChanged(); } catch (Throwable e) {}
        try { if (c != null) c.levelRenderer.allChanged(); } catch (Throwable ignored) {}
    }
    @Override protected void onEnable() { reload(Minecraft.getInstance()); }
    @Override protected void onDisable() { reload(Minecraft.getInstance()); }
}
