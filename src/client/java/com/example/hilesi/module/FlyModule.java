package com.example.hilesi.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;

/**
 * Fly - survival/creative-style flight helpers.
 * Double Jump (Space x2) toggle. Elytra Boost: elytra ile fişek olmadan ileri ivme.
 */
public class FlyModule extends Module {

    private float flySpeed = 0.05f; // 0.02 - 0.20
    private boolean doubleJump = true;
    private boolean elytraBoost = true;
    private float elytraBoostSpeed = 0.08f;

    private boolean jumpWasPressed = false;
    private long firstJumpMs = 0L;
    private boolean waitingSecond = false;
    private static final long DOUBLE_TAP_MS = 350L;

    public FlyModule() { super("Fly", "Ucus + Elytra boost (fişeksiz)"); }

    public float getFlySpeed() { return flySpeed; }
    public void setFlySpeed(float s) { this.flySpeed = clamp(s, 0.02f, 0.20f); }
    public boolean isDoubleJump() { return doubleJump; }
    public void setDoubleJump(boolean v) { this.doubleJump = v; }
    public void toggleDoubleJump() { setDoubleJump(!doubleJump); }
    public boolean isElytraBoost() { return elytraBoost; }
    public void toggleElytraBoost() { this.elytraBoost = !this.elytraBoost; }
    public float getElytraBoostSpeed() { return elytraBoostSpeed; }
    public void cycleElytraBoostSpeed(int dir) { this.elytraBoostSpeed = clamp(elytraBoostSpeed + 0.01f * dir, 0.02f, 0.30f); }

    public void cycleSpeed(int dir) { setFlySpeed(flySpeed + 0.01f * dir); }
    private static float clamp(float v, float min, float max) { return Math.max(min, Math.min(max, v)); }

    @Override
    protected void onEnable() { apply(MinecraftClient.getInstance()); }

    @Override
    protected void onDisable() {
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null && client.player != null) {
                client.player.getAbilities().allowFlying = false;
                client.player.getAbilities().flying = false;
                client.player.getAbilities().setFlySpeed(0.05f);
            }
        } catch (Throwable ignored) {}
    }

    @Override
    public void onTick(MinecraftClient client) {
        if (client == null || client.player == null) return;
        if (doubleJump && client.currentScreen == null) {
            try {
                boolean jump = client.options.jumpKey.isPressed();
                long now = System.currentTimeMillis();
                if (jump && !jumpWasPressed) {
                    if (waitingSecond && (now - firstJumpMs) <= DOUBLE_TAP_MS) { toggle(); waitingSecond = false; }
                    else { firstJumpMs = now; waitingSecond = true; }
                }
                if (waitingSecond && (now - firstJumpMs) > DOUBLE_TAP_MS) waitingSecond = false;
                jumpWasPressed = jump;
            } catch (Throwable ignored) {}
        }
        if (!isEnabled() || !client.player.isAlive()) return;

        // Elytra boost (fişeksiz): sadece W (ileri) basiliyken
        try {
            if (elytraBoost && client.player.isGliding() && client.currentScreen == null && client.options.forwardKey.isPressed()) {
                Vec3d look = client.player.getRotationVec(1.0f);
                Vec3d v = client.player.getVelocity();
                double add = elytraBoostSpeed;
                client.player.setVelocity(v.x + look.x * add, v.y + look.y * add * 0.6, v.z + look.z * add);
                return;
            }
        } catch (Throwable ignored) {}

        if (client.player.isGliding() || client.player.isSubmergedInWater() || client.player.isInLava()) return;
        apply(client);
    }

    private void apply(MinecraftClient client) {
        try {
            if (client == null || client.player == null) return;
            client.player.getAbilities().allowFlying = true;
            client.player.getAbilities().flying = true;
            client.player.getAbilities().setFlySpeed(flySpeed);
        } catch (Throwable ignored) {}
    }
}
