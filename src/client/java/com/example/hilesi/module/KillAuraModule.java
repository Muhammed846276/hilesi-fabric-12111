package com.example.hilesi.module;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/**
 * Kill Aura - otomatik saldiri + oto aim
 * Sadece kendi oyunun / offline test icin.
 */
public class KillAuraModule extends Module {

    private double range = 3.0;
    private int cps = 5;
    private int fov = 180;
    private boolean playersOnly = false;
    private boolean autoSwitch = true;
    private boolean autoAim = true;

    private long lastAttackMs = 0L;

    public KillAuraModule() {
        super("Kill Aura");
    }

    public double getRange() { return range; }
    public int getCps() { return cps; }
    public int getFov() { return fov; }
    public boolean isPlayersOnly() { return playersOnly; }
    public boolean isAutoSwitch() { return autoSwitch; }
    public boolean isAutoAim() { return autoAim; }

    public void setRange(double range) { this.range = clamp(range, 1.0, 8.0); }
    public void setCps(int cps) { this.cps = clamp(cps, 1, 20); }
    public void setFov(int fov) { this.fov = clamp(fov, 30, 360); }
    public void setPlayersOnly(boolean v) { this.playersOnly = v; }
    public void setAutoSwitch(boolean v) { this.autoSwitch = v; }
    public void setAutoAim(boolean v) { this.autoAim = v; }

    public void cycleRange(int dir) { setRange(range + 0.5 * dir); }
    public void cycleCps(int dir) { setCps(cps + dir); }
    public void cycleFov(int dir) { setFov(fov + 15 * dir); }
    public void togglePlayersOnly() { setPlayersOnly(!playersOnly); }
    public void toggleAutoSwitch() { setAutoSwitch(!autoSwitch); }
    public void toggleAutoAim() { setAutoAim(!autoAim); }

    private static double clamp(double v, double min, double max) { return Math.max(min, Math.min(max, v)); }
    private static int clamp(int v, int min, int max) { return Math.max(min, Math.min(max, v)); }

    @Override
    protected void onEnable() { lastAttackMs = 0L; }
    @Override
    protected void onDisable() {}

    @Override
    public void onTick(Minecraft client) {
        if (!isEnabled() || client == null || client.player == null || client.level == null) return;
        if (client.gui.screen() != null) return;
        if (!client.player.isAlive()) return;

        Entity target = findTarget(client);
        if (target == null || !target.isAlive()) return;

        if (autoAim) {
            try {
                aimAt(client, target);
            } catch (Throwable ignored) {}
        }

        long now = System.currentTimeMillis();
        long minDelay = (long) (1000.0 / Math.max(1, cps));
        if (now - lastAttackMs < minDelay) return;
        try {
            if (client.player.getAttackStrengthScale(0.5f) < 1.0f) return;
        } catch (Throwable ignored) {}

        try {
            if (client.gameMode == null) return;
            client.gameMode.attack(client.player, target);
            client.player.swing(InteractionHand.MAIN_HAND);
            lastAttackMs = now;
        } catch (Throwable ignored) {}
    }

    private void aimAt(Minecraft client, Entity target) {
        Player me = client.player;
        Vec3 eye = me.getEyePosition();
        double dx = target.getX() - eye.x;
        double dy = (target.getY() + target.getBbHeight() * 0.5) - eye.y;
        double dz = target.getZ() - eye.z;
        double horizontal = Math.sqrt(dx * dx + dz * dz);
        if (horizontal < 1e-4 && Math.abs(dy) < 1e-4) return;
        float yaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
        float pitch = (float) Math.toDegrees(-Math.atan2(dy, horizontal));
        try { me.setYRot(yaw); } catch (Throwable ignored) {}
        try { me.setXRot(pitch); } catch (Throwable ignored) {}
        try { me.setYHeadRot(yaw); } catch (Throwable ignored) {}
    }

    private Entity findTarget(Minecraft client) {
        Player me = client.player;
        double bestDistSq = range * range;
        Entity best = null;

        Vec3 look = null;
        try {
            look = me.getViewVector(1.0f).normalize();
        } catch (Throwable ignored) {}

        Entity bestAim = null;
        double bestAimDistSq = range * range;

        for (Entity e : client.level.entitiesForRendering()) {
            if (e == null || e == me) continue;
            if (!e.isAlive() || e.isRemoved() || e.isSpectator()) continue;
            if (!(e instanceof LivingEntity)) continue;
            LivingEntity living = (LivingEntity) e;
            try { if (living.getHealth() <= 0) continue; } catch (Throwable ignored) {}
            if (playersOnly && !(e instanceof Player)) continue;

            double distSq = me.distanceToSqr(e);
            if (distSq > range * range) continue;
            if (distSq < 0.0001) continue;

            if (distSq < bestAimDistSq) {
                bestAim = e;
                bestAimDistSq = distSq;
            }

            if (look != null && fov < 360) {
                Vec3 to = new Vec3(
                        e.getX() - me.getX(),
                        (e.getY() + e.getBbHeight() * 0.5) - (me.getY() + me.getEyeHeight()),
                        e.getZ() - me.getZ()
                );
                if (to.lengthSqr() < 1e-6) continue;
                to = to.normalize();
                double dot = clamp(look.dot(to), -1.0, 1.0);
                double angleDeg = Math.toDegrees(Math.acos(dot));
                if (angleDeg > fov / 2.0) continue;
            }
            if (distSq < bestDistSq) {
                best = e;
                bestDistSq = distSq;
            }
        }
        if (best != null) return best;
        if (autoAim) return bestAim;
        return null;
    }
}
