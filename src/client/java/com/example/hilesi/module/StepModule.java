package com.example.hilesi.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.attribute.EntityAttributes;

public class StepModule extends Module {
    private float height = 1.25f;
    public StepModule() { super("Step"); }
    public float getHeight() { return height; }
    public void setHeight(float v) { height = Math.max(0.6f, Math.min(3.0f, v)); }
    public void cycleHeight(int dir) { setHeight(height + 0.25f * dir); }
    private void apply(MinecraftClient c, float h) {
        if (c == null || c.player == null) return;
        try {
            var inst = c.player.getAttributeInstance(EntityAttributes.STEP_HEIGHT);
            if (inst != null) inst.setBaseValue(h);
        } catch (Throwable ignored) {}
    }
    @Override protected void onDisable() { apply(MinecraftClient.getInstance(), 0.6f); }
    @Override public void onTick(MinecraftClient client) { if (!isEnabled()) return; apply(client, height); }
}
