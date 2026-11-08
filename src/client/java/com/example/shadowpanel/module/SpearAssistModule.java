package com.example.shadowpanel.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

/**
 * Spear Assist - mizrak ile sag tiklaninca (10-256 blok arasi hedef varsa)
 * oyuncu/mob'a dogru hizla ilerler. Sadece kendi oyunun / offline test icin.
 */
public class SpearAssistModule extends Module {

    private double minRange = 10.0;
    private double maxRange = 256.0;
    private double boostSpeed = 3.0; // blok/tick civari itki
    private int fov = 180; // 90-180: 180 = tum yonler (arkasi dahil)
    private boolean aimLock = true;
    private boolean ignoreWalls = true;
    private boolean playersOnly = false;

    private boolean useWasPressed = false;

    public SpearAssistModule() {
        super("Spear Boost");
    }

    public double getMinRange() { return minRange; }
    public double getMaxRange() { return maxRange; }
    public double getBoostSpeed() { return boostSpeed; }
    public int getFov() { return fov; }
    public boolean isAimLock() { return aimLock; }
    public boolean isIgnoreWalls() { return ignoreWalls; }
    public boolean isPlayersOnly() { return playersOnly; }

    public void setMinRange(double v) { this.minRange = clamp(v, 1.0, 255.0); if (this.minRange > maxRange) maxRange = this.minRange; }
    public void setMaxRange(double v) { this.maxRange = clamp(v, 2.0, 256.0); if (this.maxRange < minRange) minRange = this.maxRange; }
    public void setBoostSpeed(double v) { this.boostSpeed = clamp(v, 0.5, 8.0); }
    public void setFov(int v) { this.fov = clamp(v, 90, 180); }
    public void setAimLock(boolean v) { this.aimLock = v; }
    public void setIgnoreWalls(boolean v) { this.ignoreWalls = v; }
    public void setPlayersOnly(boolean v) { this.playersOnly = v; }

    public void cycleMinRange(int dir) { setMinRange(minRange + 1.0 * dir); }
    public void cycleMaxRange(int dir) { setMaxRange(maxRange + 8.0 * dir); }
    public void cycleBoostSpeed(int dir) { setBoostSpeed(boostSpeed + 0.25 * dir); }
    public void cycleFov(int dir) { setFov(fov + 15 * dir); }
    public void toggleAimLock() { setAimLock(!aimLock); }
    public void toggleIgnoreWalls() { setIgnoreWalls(!ignoreWalls); }
    public void togglePlayersOnly() { setPlayersOnly(!playersOnly); }

    private static double clamp(double v, double min, double max) { return Math.max(min, Math.min(max, v)); }
    private static int clamp(int v, int min, int max) { return Math.max(min, Math.min(max, v)); }

    @Override
    protected void onDisable() { useWasPressed = false; }

    @Override
    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client == null || client.player == null || client.world == null) return;
        if (client.currentScreen != null) return;
        if (!client.player.isAlive()) return;

        boolean usePressed;
        try {
            usePressed = client.options.useKey.isPressed();
        } catch (Throwable t) {
            return;
        }
        boolean justPressed = usePressed && !useWasPressed;
        useWasPressed = usePressed;

        // Sag tik basili tutulurken calisir (baslangicta tetik)
        if (!usePressed) return;
        if (!holdingSpear(client)) return;

        Entity target = findTarget(client);
        if (target == null) return;

        try {
            if (aimLock) aimAt(client, target);
        } catch (Throwable ignored) {}
        try {
            boostToward(client, target, justPressed);
        } catch (Throwable ignored) {}
    }

    private boolean holdingSpear(MinecraftClient client) {
        try {
            if (isSpear(client.player.getMainHandStack())) return true;
            if (isSpear(client.player.getOffHandStack())) return true;
        } catch (Throwable ignored) {}
        return false;
    }

    private boolean isSpear(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        try {
            Identifier id = Registries.ITEM.getId(stack.getItem());
            if (id == null) return false;
            String path = id.getPath();
            if (path == null) return false;
            // 1.21.11+ spear itemleri (wooden_spear, iron_spear, ..._spear)
            return path.equals("spear") || path.endsWith("_spear");
        } catch (Throwable ignored) {}
        return false;
    }

    private Entity findTarget(MinecraftClient client) {
        PlayerEntity me = client.player;
        double minSq = minRange * minRange;
        double maxSq = maxRange * maxRange;
        Entity best = null;
        double bestDistSq = Double.MAX_VALUE;

        Vec3d look = null;
        try {
            look = me.getRotationVec(1.0f).normalize();
        } catch (Throwable ignored) {}

        for (Entity e : client.world.getEntities()) {
            if (e == null || e == me) continue;
            if (!e.isAlive() || e.isRemoved() || e.isSpectator()) continue;
            if (!(e instanceof LivingEntity)) continue;
            LivingEntity living = (LivingEntity) e;
            if (living.getHealth() <= 0) continue;
            if (playersOnly && !(e instanceof PlayerEntity)) continue;

            double distSq = me.squaredDistanceTo(e);
            if (distSq < minSq || distSq > maxSq) continue;

            // Aci/FOV: 180 = tum yonler (arkasi dahil), <180 = bakis yonu konisi
            if (look != null && fov < 180) {
                Vec3d to = new Vec3d(
                        e.getX() - me.getX(),
                        (e.getY() + e.getHeight() * 0.5) - (me.getY() + me.getStandingEyeHeight()),
                        e.getZ() - me.getZ()
                );
                if (to.lengthSquared() < 1e-6) continue;
                to = to.normalize();
                double dot = clamp(look.dotProduct(to), -1.0, 1.0);
                double angleDeg = Math.toDegrees(Math.acos(dot));
                if (angleDeg > fov / 2.0) continue;
            }

            // Duvar kontrolu: ignoreWalls aciksa yoksay
            if (!ignoreWalls) {
                try {
                    if (!me.canSee(e)) continue;
                } catch (Throwable ignored) {}
            }

            if (distSq < bestDistSq) {
                best = e;
                bestDistSq = distSq;
            }
        }
        return best;
    }

    private void aimAt(MinecraftClient client, Entity target) {
        PlayerEntity me = client.player;
        Vec3d eye = me.getEyePos();
        double dx = target.getX() - eye.getX();
        double dy = (target.getY() + target.getHeight() * 0.5) - eye.getY();
        double dz = target.getZ() - eye.getZ();
        double horizontal = Math.sqrt(dx * dx + dz * dz);
        if (horizontal < 1e-4 && Math.abs(dy) < 1e-4) return;
        float yaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
        float pitch = (float) Math.toDegrees(-Math.atan2(dy, horizontal));
        me.setYaw(yaw);
        me.setPitch(pitch);
    }

    private void boostToward(MinecraftClient client, Entity target, boolean initial) {
        PlayerEntity me = client.player;
        Vec3d from = new Vec3d(me.getX(), me.getY() + me.getStandingEyeHeight() * 0.5, me.getZ());
        Vec3d to = new Vec3d(target.getX(), target.getY() + target.getHeight() * 0.5, target.getZ());
        Vec3d delta = to.subtract(from);
        double len = delta.length();
        if (len < 1e-4) return;
        Vec3d dir = delta.multiply(1.0 / len);

        // Hedefe cok yaklasildiysa asiri itme
        double speed = boostSpeed;
        if (len < minRange) speed = Math.min(speed, 0.8);
        Vec3d vel = dir.multiply(speed);
        // Hafif yukari bilesen: yere takilmayi azalt
        if (initial || me.isOnGround()) {
            vel = new Vec3d(vel.x, Math.max(vel.y, 0.12), vel.z);
        }
        me.setVelocity(vel);
        try {
            me.velocityDirty = true;
        } catch (Throwable ignored) {}
        try {
            me.fallDistance = 0.0f;
        } catch (Throwable ignored) {}
    }
}
