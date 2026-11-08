package com.example.shadowpanel.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;

public class SpeedModule extends Module {
    private double multiplier = 1.2;
    public SpeedModule() { super("Speed"); }
    public double getMultiplier() { return multiplier; }
    public void setMultiplier(double v) { multiplier = Math.max(1.0, Math.min(3.0, v)); }
    public void cycleMultiplier(int dir) { setMultiplier(multiplier + 0.1 * dir); }
    @Override
    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client == null || client.player == null) return;
        if (client.currentScreen != null) return;
        if (!client.player.isOnGround()) return;
        try {
            Vec3d v = client.player.getVelocity();
            double mx = client.player.input.getMovementInput().x;
            double my = client.player.input.getMovementInput().y;
            if (mx == 0 && my == 0) return;
            float yaw = client.player.getYaw();
            double rad = Math.toRadians(yaw);
            double moveX = -Math.sin(rad) * my * 0.1 * multiplier + Math.cos(rad) * mx * 0.1 * multiplier;
            double moveZ = Math.cos(rad) * my * 0.1 * multiplier + Math.sin(rad) * mx * 0.1 * multiplier;
            client.player.setVelocity(v.x + moveX, v.y, v.z + moveZ);
        } catch (Throwable ignored) {}
    }
}
