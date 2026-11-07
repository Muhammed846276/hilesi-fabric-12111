package com.example.hilesi.module;

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
    private boolean playersOnly = false;

    private boolean useWasPressed = false;

    public SpearAssistModule() {
        super("Spear Boost");
    }

    public double getMinRange() { return minRange; }
    public double getMaxRange() { return maxRange; }
    public double getBoostSpeed() { return boostSpeed; }
    public boolean isPlayersOnly() { return playersOnly; }

    public void setMinRange(double v) { this.minRange = clamp(v, 1.0, 255.0); if (this.minRange > maxRange) maxRange = this.minRange; }
    public void setMaxRange(double v) { this.maxRange = clamp(v, 2.0, 256.0); if (this.maxRange < minRange) minRange = this.maxRange; }
    public void setBoostSpeed(double v) { this.boostSpeed = clamp(v, 0.5, 8.0); }
    public void setPlayersOnly(boolean v) { this.playersOnly = v; }

    public void cycleMinRange(int dir) { setMinRange(minRange + 1.0 * dir); }
    public void cycleMaxRange(int dir) { setMaxRange(maxRange + 8.0 * dir); }
    public void cycleBoostSpeed(int dir) { setBoostSpeed(boostSpeed + 0.25 * dir); }
    public void togglePlayersOnly() { setPlayersOnly(!playersOnly); }

    private static double clamp(double v, double min, double max) { return Math.max(min, Math.min(max, v)); }

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

        for (Entity e : client.world.getEntities()) {
            if (e == null || e == me) continue;
            if (!e.isAlive() || e.isRemoved() || e.isSpectator()) continue;
            if (!(e instanceof LivingEntity)) continue;
            if (((LivingEntity) e).getHealth() <= 0) continue;
            if (playersOnly && !(e instanceof PlayerEntity)) continue;

            double distSq = me.squaredDistanceTo(e);
            if (distSq < minSq || distSq > maxSq) continue;
            if (distSq < bestDistSq) {
                best = e;
                bestDistSq = distSq;
            }
        }
        return best;
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
