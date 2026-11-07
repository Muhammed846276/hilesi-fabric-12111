package com.example.hilesi.module;

import net.minecraft.client.Minecraft;

/**
 * Fly - survival modda ucmayi deneme (sadece kendi oyunun / offline)
 * Double Jump (Space x2) ile ac/kapa (ayarlanabilir).
 */
public class FlyModule extends Module {

    private float flySpeed = 0.05f; // 0.02 - 0.20
    private boolean doubleJump = true;

    private boolean jumpWasPressed = false;
    private long firstJumpMs = 0L;
    private boolean waitingSecond = false;
    private static final long DOUBLE_TAP_MS = 350L;

    public FlyModule() {
        super("Fly");
    }

    public float getFlySpeed() { return flySpeed; }
    public void setFlySpeed(float s) { this.flySpeed = clamp(s, 0.02f, 0.20f); }
    public boolean isDoubleJump() { return doubleJump; }
    public void setDoubleJump(boolean v) { this.doubleJump = v; }
    public void toggleDoubleJump() { setDoubleJump(!doubleJump); }

    public void cycleSpeed(int dir) { setFlySpeed(flySpeed + 0.01f * dir); }
    public void setSpeedPercent(int pct) {
        float v = 0.02f + (0.20f - 0.02f) * (pct / 100f);
        setFlySpeed(v);
    }

    private static float clamp(float v, float min, float max) { return Math.max(min, Math.min(max, v)); }

    @Override
    protected void onEnable() {
        Minecraft client = Minecraft.getInstance();
        apply(client);
    }

    @Override
    protected void onDisable() {
        Minecraft client = Minecraft.getInstance();
        try {
            if (client != null && client.player != null) {
                client.player.getAbilities().mayfly = false;
                client.player.getAbilities().flying = false;
                client.player.getAbilities().setFlyingSpeed(0.05f);
            }
        } catch (Throwable ignored) {}
    }

    @Override
    public void onTick(Minecraft client) {
        if (client == null || client.player == null) return;
        if (doubleJump && client.gui.screen() == null) {
            try {
                boolean jump = client.options.keyJump.isDown();
                long now = System.currentTimeMillis();
                if (jump && !jumpWasPressed) {
                    if (waitingSecond && (now - firstJumpMs) <= DOUBLE_TAP_MS) {
                        toggle();
                        waitingSecond = false;
                    } else {
                        firstJumpMs = now;
                        waitingSecond = true;
                    }
                }
                if (waitingSecond && (now - firstJumpMs) > DOUBLE_TAP_MS) {
                    waitingSecond = false;
                }
                jumpWasPressed = jump;
            } catch (Throwable ignored) {}
        }
        if (!isEnabled()) return;
        if (!client.player.isAlive()) return;
        if (client.player.isFallFlying() || client.player.isInWater() || client.player.isInLava()) return;
        apply(client);
    }

    private void apply(Minecraft client) {
        try {
            if (client.player == null) return;
            client.player.getAbilities().mayfly = true;
            client.player.getAbilities().flying = true;
            client.player.getAbilities().setFlyingSpeed(flySpeed);
        } catch (Throwable ignored) {}
    }
}
