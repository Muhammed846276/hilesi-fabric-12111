package com.example.hilesi.module;

import net.minecraft.client.MinecraftClient;

/**
 * XRay (client-side). Not: Paper/Orebfuscator gibi sunucu anti-xray aktifse
 * sunucu gercek cevher verisini gondermez; bu durumda client XRay onu "asamaz".
 * Bu modül sadece istemcinin bildigi verilere gore gorunum/yardimci davranis saglar.
 */
public class XRayModule extends Module {
    private boolean caveMode = false;
    public XRayModule() { super("XRay"); }
    public boolean isCaveMode() { return caveMode; }
    public void toggleCaveMode() { caveMode = !caveMode; }
    @Override
    protected void onEnable() {
        try { var c = MinecraftClient.getInstance(); if (c != null && c.worldRenderer != null) c.worldRenderer.reload(); } catch (Throwable ignored) {}
    }
    @Override
    protected void onDisable() {
        try { var c = MinecraftClient.getInstance(); if (c != null && c.worldRenderer != null) c.worldRenderer.reload(); } catch (Throwable ignored) {}
    }
    @Override
    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client == null) return;
        // Mixin tabanli tam XRay (blok saydamligi/culling) icin client mixin gerekir.
        // Temel toggle + reload burada saglanir; advanced mixin batch'te eklenebilir.
    }
}
