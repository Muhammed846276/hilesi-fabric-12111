package com.example.hilesi.module;

import net.minecraft.client.MinecraftClient;

public class StepModule extends Module {
    private float height = 1.25f;
    public StepModule() { super("Step"); }
    public float getHeight() { return height; }
    public void setHeight(float v) { height = Math.max(0.6f, Math.min(3.0f, v)); }
    public void cycleHeight(int dir) { setHeight(height + 0.25f * dir); }
    @Override
    protected void onDisable() {
        try { var c = MinecraftClient.getInstance(); if (c!=null && c.player!=null) c.player.setStepHeight(0.6f); } catch (Throwable ignored) {}
    }
    @Override
    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client == null || client.player == null) return;
        try { client.player.setStepHeight(height); } catch (Throwable ignored) {}
    }
}
