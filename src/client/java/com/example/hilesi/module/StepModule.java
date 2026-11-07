package com.example.hilesi.module;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class StepModule extends Module {
    private float height = 1.25f;
    public StepModule() { super("Step"); }
    public float getHeight() { return height; }
    public void setHeight(float v) { height = Math.max(0.6f, Math.min(3.0f, v)); }
    public void cycleHeight(int dir) { setHeight(height + 0.25f * dir); }
    private void apply(Minecraft c, float h) {
        if (c == null || c.player == null) return;
        try {
            var inst = c.player.getAttributeInstance(Attributes.STEP_HEIGHT);
            if (inst != null) inst.setBaseValue(h);
        } catch (Throwable ignored) {}
    }
    @Override protected void onDisable() { apply(Minecraft.getInstance(), 0.6f); }
    @Override public void onTick(Minecraft client) { if (!isEnabled()) return; apply(client, height); }
}
