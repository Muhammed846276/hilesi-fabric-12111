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
 * Auto Mace (Adim D)
 * Mace'e otomatik gecer, oto aim ile hedefe vurur.
 * Stun Slam: hedef yakin ve yerdeyse smash icin ziplar.
 * Sadece kendi oyunun / offline test icin.
 */
public class AutoMaceModule extends Module {

    private double range = 3.0;
    private int cps = 5;
    private int fov = 180;
    private boolean playersOnly = false;
    private boolean maceSwitch = true;
    private boolean autoAim = true;
    private boolean stunSlam = true;

    private long lastAttackMs = 0L;

    public AutoMaceModule() {
        super("Auto Mace");
    }

    public double getRange() { return range; }
    public int getCps() { return cps; }
    public int getFov() { return fov; }
    public boolean isPlayersOnly() { return playersOnly; }
    public boolean isMaceSwitch() { return maceSwitch; }
    public boolean isAutoAim() { return autoAim; }
    public boolean isStunSlam() { return stunSlam; }

    public void setRange(double v) { this.range = clamp(v, 3.0, 8.0); }
    public void setCps(int v) { this.cps = clamp(v, 1, 20); }
    public void setFov(int v) { this.fov = clamp(v, 30, 360); }
    public void setPlayersOnly(boolean v) { this.playersOnly = v; }
    public void setMaceSwitch(boolean v) { this.maceSwitch = v; }
    public void setAutoAim(boolean v) { this.autoAim = v; }
    public void setStunSlam(boolean v) { this.stunSlam = v; }

    public void cycleRange(int d) { setRange(range + 0.5 * d); }
    public void cycleCps(int d) { setCps(cps + d); }
    public void cycleFov(int d) { setFov(fov + 15 * d); }
    public void togglePlayersOnly() { setPlayersOnly(!playersOnly); }
    public void toggleMaceSwitch() { setMaceSwitch(!maceSwitch); }
    public void toggleAutoAim() { setAutoAim(!autoAim); }
    public void toggleStunSlam() { setStunSlam(!stunSlam); }

    private static double clamp(double v, double a, double b) { return Math.max(a, Math.min(b, v)); }
    private static int clamp(int v, int a, int b) { return Math.max(a, Math.min(b, v)); }

    @Override
    protected void onEnable() { lastAttackMs = 0L; }
    @Override
    protected void onDisable() {}

    @Override
    public void onTick(MinecraftClient client) {
        if (!isEnabled() || client == null || client.player == null || client.world == null) return;
        if (client.currentScreen != null) return;
        if (!client.player.isAlive()) return;

        // 1) Mace'e gec
        if (maceSwitch) {
            try { switchToMace(client); } catch (Throwable ignored) {}
        }

        Entity target = findTarget(client);
        if (target == null || !target.isAlive()) return;

        if (autoAim) {
            try { aimAt(client, target); } catch (Throwable ignored) {}
        }

        if (stunSlam) {
            try {
                double dist = client.player.distanceTo(target);
                if (dist <= range && client.player.isOnGround()) {
                    client.player.jump();
                }
            } catch (Throwable ignored) {}
        }

        long now = System.currentTimeMillis();
        long minDelay = (long) (1000.0 / Math.max(1, cps));
        if (now - lastAttackMs < minDelay) return;
        try {
            if (client.player.getAttackCooldownProgress(0.5f) < 1.0f) return;
        } catch (Throwable ignored) {}

        try {
            if (client.interactionManager == null) return;
            client.interactionManager.attackEntity(client.player, target);
            client.player.swingHand(Hand.MAIN_HAND);
            lastAttackMs = now;
        } catch (Throwable ignored) {}
    }

    private void switchToMace(MinecraftClient client) {
        PlayerEntity me = client.player;
        ItemStack held = me.getMainHandStack();
        if (held != null && held.getItem() == Items.MACE) return;
        for (int i = 0; i < 9; i++) {
            ItemStack s = me.getInventory().getStack(i);
            if (s != null && s.getItem() == Items.MACE) {
                // 1.21.11'de selectedSlot alanina yaz
                me.getInventory().selectedSlot = i;
                return;
            }
        }
    }

    private void aimAt(MinecraftClient client, Entity target) {
        PlayerEntity me = client.player;
        Vec3d eye = me.getEyePos();
        double dx = target.getX() - eye.getX();
        double dy = (target.getY() + target.getHeight() * 0.5) - eye.getY();
        double dz = target.getZ() - eye.getZ();
        double h = Math.sqrt(dx * dx + dz * dz);
        if (h < 1e-4 && Math.abs(dy) < 1e-4) return;
        float yaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
        float pitch = (float) Math.toDegrees(-Math.atan2(dy, h));
        me.setYaw(yaw);
        me.setPitch(pitch);
    }

    private Entity findTarget(MinecraftClient client) {
        PlayerEntity me = client.player;
        double bestDistSq = range * range;
        Entity best = null;
        Vec3d look = null;
        try { look = me.getRotationVec(1.0f).normalize(); } catch (Throwable ignored) {}
        Entity bestAim = null;
        double bestAimSq = range * range;
        for (Entity e : client.world.getEntities()) {
            if (e == null || e == me) continue;
            if (!e.isAlive() || e.isRemoved() || e.isSpectator()) continue;
            if (!(e instanceof LivingEntity)) continue;
            LivingEntity l = (LivingEntity) e;
            if (l.getHealth() <= 0) continue;
            if (playersOnly && !(e instanceof PlayerEntity)) continue;
            double ds = me.squaredDistanceTo(e);
            if (ds > range * range) continue;
            if (ds < 0.0001) continue;
            if (ds < bestAimSq) { bestAim = e; bestAimSq = ds; }
            if (look != null && fov < 360) {
                Vec3d to = new Vec3d(
                        e.getX() - me.getX(),
                        (e.getY() + e.getHeight() * 0.5) - (me.getY() + me.getHeight() * 0.5),
                        e.getZ() - me.getZ());
                if (to.lengthSquared() < 1e-6) continue;
                to = to.normalize();
                double dot = clamp(look.dotProduct(to), -1.0, 1.0);
                double ang = Math.toDegrees(Math.acos(dot));
                if (ang > fov / 2.0) continue;
            }
            if (ds < bestDistSq) { best = e; bestDistSq = ds; }
        }
        if (best != null) return best;
        if (autoAim) return bestAim;
        return null;
    }
}
