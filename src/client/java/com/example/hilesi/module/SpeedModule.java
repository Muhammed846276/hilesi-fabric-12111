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
        if (client.screen != null) return;
        if (!client.player.onGround()) return;
        try {
            Vec3 v = client.player.getDeltaMovement();
            float fwd = 0, side = 0;
            try { fwd = client.player.input.forwardImpulse; side = client.player.input.leftImpulse; } catch(Throwable e){}
            if (fwd == 0 && side == 0) return;
            float yaw = client.player.getYRot();
            double rad = Math.toRadians(yaw);
            double moveX = -Math.sin(rad) * fwd * 0.1 * multiplier + Math.cos(rad) * side * 0.1 * multiplier;
            double moveZ = Math.cos(rad) * fwd * 0.1 * multiplier + Math.sin(rad) * side * 0.1 * multiplier;
            client.player.setDeltaMovement(v.x + moveX, v.y, v.z + moveZ);
        } catch (Throwable ignored) {}
    }
}
