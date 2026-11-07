package com.example.hilesi.module;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;

public class SpeedModule extends Module {
    private double multiplier = 1.2;
    public SpeedModule() { super("Speed"); }
    public double getMultiplier() { return multiplier; }
    public void setMultiplier(double v) { multiplier = Math.max(1.0, Math.min(3.0, v)); }
    public void cycleMultiplier(int dir) { setMultiplier(multiplier + 0.1 * dir); }
    @Override
    public void onTick(Minecraft client) {
        if (!isEnabled() || client == null || client.player == null) return;
        if (client.gui.screen() != null) return;
        if (!client.player.onGround()) return;
        try {
            Vec3 v = client.player.getDeltaMovement();
            boolean fwd = client.options.keyUp.isDown();
            boolean back = client.options.keyDown.isDown();
            boolean left = client.options.keyLeft.isDown();
            boolean right = client.options.keyRight.isDown();
            if (!(fwd||back||left||right)) return;
            float yaw = client.player.getYRot();
            double rad = Math.toRadians(yaw);
            double my = (fwd?1:0) - (back?1:0);
            double mx = (left?1:0) - (right?1:0);
            double moveX = -Math.sin(rad) * my * 0.1 * multiplier + Math.cos(rad) * mx * 0.1 * multiplier;
            double moveZ = Math.cos(rad) * my * 0.1 * multiplier + Math.sin(rad) * mx * 0.1 * multiplier;
            client.player.setDeltaMovement(v.x + moveX, v.y, v.z + moveZ);
        } catch (Throwable ignored) {}
    }
}
