package com.example.hilesi.module;

import net.minecraft.client.Minecraft;

public class StepModule extends Module {
    private float height = 1.25f;
    public StepModule() { super("Step"); }
    public float getHeight() { return height; }
    public void setHeight(float v) { height = Math.max(0.6f, Math.min(3.0f, v)); }
    public void cycleHeight(int dir) { setHeight(height + 0.25f * dir); }
    private void apply(Minecraft c, float h) {
        if (c == null || c.player == null) return;
        try {
            for (var m : c.player.getClass().getMethods()) {
                if (m.getName().equals("getAttribute") && m.getParameterCount()==1) {
                    Object inst = m.invoke(c.player, net.minecraft.world.entity.ai.attributes.Attributes.STEP_HEIGHT);
                    if (inst != null) { inst.getClass().getMethod("setBaseValue", double.class).invoke(inst, (double)h); return; }
                }
            }
        } catch (Throwable ignored) {}
    }
    @Override protected void onDisable() { apply(Minecraft.getInstance(), 0.6f); }
    @Override public void onTick(Minecraft client) { if (!isEnabled()) return; apply(client, height); }
}
