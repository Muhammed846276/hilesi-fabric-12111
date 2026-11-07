package com.example.hilesi.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

/**
 * Kill Aura - otomatik saldiri (Adim B)
 * Menzil / FOV / CPS kontrolleriyle en yakin gecerli hedefe vurur.
 * Sadece kendi oyunun / offline test icin tasarlandi.
 */
public class KillAuraModule extends Module {

    private double range = 3.0;
    private int cps = 5;
    private int fov = 180;
    private boolean playersOnly = false;
    private boolean autoSwitch = true;

    private long lastAttackMs = 0L;

    public KillAuraModule() {
        super("Kill Aura");
    }

    public double getRange() { return range; }
    public int getCps() { return cps; }
    public int getFov() { return fov; }
    public boolean isPlayersOnly() { return playersOnly; }
    public boolean isAutoSwitch() { return autoSwitch; }

    public void setRange(double range) { this.range = clamp(range, 3.0, 8.0); }
    public void setCps(int cps) { this.cps = clamp(cps, 1, 20); }
    public void setFov(int fov) { this.fov = clamp(fov, 30, 360); }
    public void setPlayersOnly(boolean v) { this.playersOnly = v; }
    public void setAutoSwitch(boolean v) { this.autoSwitch = v; }

    public void cycleRange(int dir) { setRange(range + 0.5 * dir); }
    public void cycleCps(int dir) { setCps(cps + dir); }
    public void cycleFov(int dir) { setFov(fov + 15 * dir); }
    public void togglePlayersOnly() { setPlayersOnly(!playersOnly); }
    public void toggleAutoSwitch() { setAutoSwitch(!autoSwitch); }

    private static double clamp(double v, double min, double max) { return Math.max(min, Math.min(max, v)); }
    private static int clamp(int v, int min, int max) { return Math.max(min, Math.min(max, v)); }

    @Override
    protected void onEnable() { lastAttackMs = 0L; }
    @Override
    protected void onDisable() {}

    @Override
    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client == null || client.player == null || client.world == null) return;
        if (client.currentScreen != null) return;
        if (!client.player.isAlive()) return;

        // 1) Hedef sec
        Entity target = findTarget(client);
        if (target == null || !target.isAlive()) return;

        // 2) CPS gecikmesi
        long now = System.currentTimeMillis();
        long minDelay = (long) (1000.0 / Math.max(1, cps));
        if (now - lastAttackMs < minDelay) return;

        // 3) Saldiri bekleme suresi dolmadiysa bekle (zayif vurus)
        try {
            if (client.player.getAttackCooldownProgress(0.5f) < 1.0f) return;
        } catch (Throwable ignored) {}

        // 4) Vur
        try {
            if (client.interactionManager == null) return;
            client.interactionManager.attackEntity(client.player, target);
            client.player.swingHand(Hand.MAIN_HAND);
            lastAttackMs = now;
        } catch (Throwable ignored) {}
    }

    private Entity findTarget(MinecraftClient client) {
        PlayerEntity me = client.player;
        double bestDistSq = range * range;
        Entity best = null;

        Vec3d look;
        try {
            look = me.getRotationVec(1.0f).normalize();
        } catch (Throwable t) {
            look = null;
        }

        for (Entity e : client.world.getEntities()) {
            if (e == null || e == me) continue;
            if (!e.isAlive() || e.isRemoved() || e.isSpectator()) continue;

            // Sadece canli varliklar (oyuncu / mob)
            if (!(e instanceof LivingEntity)) continue;
            LivingEntity living = (LivingEntity) e;
            if (living.getHealth() <= 0) continue;

            if (playersOnly && !(e instanceof PlayerEntity)) continue;

            double distSq = me.squaredDistanceTo(e);
            if (distSq > bestDistSq) continue;
            if (distSq < 0.0001) continue;

            // FOV kontrolu
            if (look != null && fov < 360) {
                Vec3d to = new Vec3d(
                        e.getX() - me.getX(),
                        (e.getY() + e.getHeight() * 0.5) - (me.getY() + me.getHeight() * 0.5),
                        e.getZ() - me.getZ()
                );
                if (to.lengthSquared() < 1e-6) continue;
                to = to.normalize();
                double dot = clamp(look.dotProduct(to), -1.0, 1.0);
                double angleDeg = Math.toDegrees(Math.acos(dot));
                if (angleDeg > fov / 2.0) continue;
            }

            best = e;
            bestDistSq = distSq;
        }
        return best;
    }
}
